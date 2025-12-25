package com.hse.paymentservice.scheduler;

import com.hse.paymentservice.model.PaymentOutbox;
import com.hse.paymentservice.repository.PaymentOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentOutboxPoller {

    private final PaymentOutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String PAYMENT_PROCESSED_TOPIC = "payment.processed.event";

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void processOutbox() {
        List<PaymentOutbox> pendingEvents =
                outboxRepository.findByStatus(PaymentOutbox.Status.PENDING);

        if (pendingEvents.isEmpty()) {
            log.debug("No pending payment events");
            return;
        }

        log.info("Processing {} pending payment events", pendingEvents.size());

        for (PaymentOutbox event : pendingEvents) {
            try {
                kafkaTemplate.send(PAYMENT_PROCESSED_TOPIC, event.getPayload())
                        .whenComplete((result, ex) -> {
                            if (ex == null) {
                                log.info("Sent payment result for order: {}", event.getOrderId());
                                event.setStatus(PaymentOutbox.Status.SENT);
                                event.setSentAt(LocalDateTime.now());
                                outboxRepository.save(event);
                            } else {
                                log.error("Failed to send payment result for order: {}",
                                        event.getOrderId(), ex);
                            }
                        });
            } catch (Exception e) {
                log.error("Error sending payment event: {}", event.getId(), e);
            }
        }
    }
}