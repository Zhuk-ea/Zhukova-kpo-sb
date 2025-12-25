package com.hse.paymentservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hse.paymentservice.event.OrderCreatedEvent;
import com.hse.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentKafkaConsumer {

    private final ObjectMapper objectMapper;
    private final PaymentService paymentService;

    @KafkaListener(topics = "${app.topics.order-created}", groupId = "payment-service-group")
    public void consumeOrderCreatedEvent(String message) {
        try {
            OrderCreatedEvent event = objectMapper.readValue(message, OrderCreatedEvent.class);
            log.info("Received OrderCreatedEvent: orderId={}, userId={}, amount={}",
                    event.getOrderId(), event.getUserId(), event.getAmount());

            paymentService.processPayment(event);

        } catch (JsonProcessingException e) {
            log.error("Failed to parse OrderCreatedEvent: {}", message, e);
        } catch (Exception e) {
            log.error("Error processing payment for event: {}", message, e);
        }
    }
}