package com.hse.orderservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hse.orderservice.event.PaymentProcessedEvent;
import com.hse.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentResultConsumer {

    private final ObjectMapper objectMapper;
    private final OrderService orderService;

    @KafkaListener(topics = "${app.topics.payment-processed}", groupId = "order-service-group")
    public void consumePaymentProcessedEvent(String message) {
        try {
            PaymentProcessedEvent event = objectMapper.readValue(message, PaymentProcessedEvent.class);
            log.info("Received PaymentProcessedEvent: orderId={}, success={}",
                    event.getOrderId(), event.isSuccess());

            orderService.updateOrderStatus(event.getOrderId(), event.isSuccess());

        } catch (JsonProcessingException e) {
            log.error("Failed to parse PaymentProcessedEvent: {}", message, e);
        } catch (Exception e) {
            log.error("Error updating order status for event: {}", message, e);
        }
    }
}