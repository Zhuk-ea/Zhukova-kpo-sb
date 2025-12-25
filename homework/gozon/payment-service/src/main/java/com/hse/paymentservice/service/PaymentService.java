package com.hse.paymentservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hse.paymentservice.event.OrderCreatedEvent;
import com.hse.paymentservice.event.PaymentProcessedEvent;
import com.hse.paymentservice.model.Account;
import com.hse.paymentservice.model.PaymentInbox;
import com.hse.paymentservice.model.PaymentOutbox;
import com.hse.paymentservice.repository.AccountRepository;
import com.hse.paymentservice.repository.PaymentInboxRepository;
import com.hse.paymentservice.repository.PaymentOutboxRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final AccountRepository accountRepository;
    private final PaymentInboxRepository inboxRepository;
    private final PaymentOutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String PAYMENT_PROCESSED_TOPIC = "payment.processed.event";

    @Transactional
    public void processPayment(OrderCreatedEvent event) throws JsonProcessingException {
        String idempotencyKey = event.getOrderId().toString();

        if (inboxRepository.existsByIdempotencyKey(idempotencyKey)) {
            log.info("Payment already processed for order: {}", event.getOrderId());
            return;
        }

        log.info("Processing payment for order: {}, user: {}, amount: {}",
                event.getOrderId(), event.getUserId(), event.getAmount());

        PaymentProcessedEvent resultEvent = new PaymentProcessedEvent();
        resultEvent.setOrderId(event.getOrderId());
        resultEvent.setUserId(event.getUserId());

        try {
            Account account = getOrCreateAccount(event.getUserId());

            boolean success = debitAccount(account, event.getAmount());

            if (success) {
                resultEvent.setSuccess(true);
                log.info("Payment successful for order: {}", event.getOrderId());
            } else {
                resultEvent.setSuccess(false);
                resultEvent.setFailureReason("Insufficient funds");
                log.warn("Insufficient funds for order: {}", event.getOrderId());
            }

        } catch (Exception e) {
            resultEvent.setSuccess(false);
            resultEvent.setFailureReason("Internal error: " + e.getMessage());
            log.error("Error processing payment for order: {}", event.getOrderId(), e);
        }

        saveToInbox(idempotencyKey, event, resultEvent);

        saveToOutbox(event, resultEvent);
    }

    private Account getOrCreateAccount(Long userId) {
        return accountRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Account newAccount = new Account();
                    newAccount.setUserId(userId);
                    newAccount.setBalance(BigDecimal.ZERO);
                    return accountRepository.save(newAccount);
                });
    }

    private boolean debitAccount(Account account, BigDecimal amount) {
        int updatedRows = accountRepository.debitAccount(
                account.getUserId(),
                amount,
                account.getVersion()
        );
        return updatedRows > 0;
    }

    private void saveToInbox(String idempotencyKey, OrderCreatedEvent orderEvent,
                             PaymentProcessedEvent resultEvent) {
        PaymentInbox inbox = new PaymentInbox();
        inbox.setIdempotencyKey(idempotencyKey);
        inbox.setOrderId(orderEvent.getOrderId());
        inbox.setUserId(orderEvent.getUserId());
        inbox.setStatus(resultEvent.isSuccess() ? "PROCESSED" : "FAILED");

        inboxRepository.save(inbox);
        log.debug("Saved to inbox: {}", idempotencyKey);
    }

    private void saveToOutbox(OrderCreatedEvent orderEvent,
                              PaymentProcessedEvent resultEvent) throws JsonProcessingException {
        PaymentOutbox outbox = new PaymentOutbox();
        outbox.setOrderId(orderEvent.getOrderId());
        outbox.setUserId(orderEvent.getUserId());
        outbox.setPayload(objectMapper.writeValueAsString(resultEvent));
        outbox.setStatus(PaymentOutbox.Status.PENDING);

        outboxRepository.save(outbox);
        log.debug("Saved to outbox for order: {}", orderEvent.getOrderId());
    }

    @Transactional
    public void deposit(Long userId, BigDecimal amount) {
        Account account = getOrCreateAccount(userId);
        int updatedRows = accountRepository.creditAccount(
                userId, amount, account.getVersion());

        if (updatedRows > 0) {
            log.info("Deposited {} for user: {}", amount, userId);
        } else {
            log.error("Failed to deposit for user: {}", userId);
            throw new RuntimeException("Deposit failed");
        }
    }

    public BigDecimal getBalance(Long userId) {
        return accountRepository.findByUserId(userId)
                .map(Account::getBalance)
                .orElse(BigDecimal.ZERO);
    }
}