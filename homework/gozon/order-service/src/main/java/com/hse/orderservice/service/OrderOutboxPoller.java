package com.hse.orderservice.service;

import com.hse.orderservice.model.OrderOutbox;
import com.hse.orderservice.repository.OrderOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderOutboxPoller {

    private final OrderOutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String ORDER_CREATED_TOPIC = "order.created.event";

    @Scheduled(fixedDelay = 5000) // 5000 мс = 5 секунд
    @Transactional
    public void processPendingEvents() {
        log.debug("Checking for pending outbox events...");

        List<OrderOutbox> pendingEvents = outboxRepository.findByStatus(OrderOutbox.Status.PENDING);

        if (pendingEvents.isEmpty()) {
            log.debug("No pending events found");
            return;
        }

        log.info("Found {} pending events to send", pendingEvents.size());

        for (OrderOutbox event : pendingEvents) {
            try {
                kafkaTemplate.send(ORDER_CREATED_TOPIC, event.getPayload())
                        .whenComplete((result, ex) -> {
                            if (ex == null) {
                                log.info("Successfully sent event {} to Kafka. Offset: {}",
                                        event.getId(), result.getRecordMetadata().offset());
                            } else {
                                log.error("Failed to send event {} to Kafka", event.getId(), ex);
                            }
                        });
                event.setStatus(OrderOutbox.Status.SENT);
                event.setSentAt(LocalDateTime.now());
                outboxRepository.save(event);

                log.info("Marked event {} as SENT", event.getId());

            } catch (Exception e) {
                log.error("Error processing outbox event: {}", event.getId(), e);
            }
        }
    }
}