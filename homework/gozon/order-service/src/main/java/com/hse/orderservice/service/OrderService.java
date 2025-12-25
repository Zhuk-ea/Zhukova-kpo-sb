package com.hse.orderservice.service;

import com.hse.orderservice.event.OrderCreatedEvent;
import com.hse.orderservice.model.Order;
import com.hse.orderservice.model.OrderOutbox;
import com.hse.orderservice.repository.OrderOutboxRepository;
import com.hse.orderservice.repository.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper; // Для сериализации события в JSON

    @Transactional
    public Order createOrder(Long userId, BigDecimal amount, String description) throws JsonProcessingException {
        log.info("Creating order for user: {}, amount: {}", userId, amount);

        Order order = new Order();
        order.setUserId(userId);
        order.setAmount(amount);
        order.setDescription(description);
        order.setStatus(Order.OrderStatus.NEW);

        Order savedOrder = orderRepository.save(order);
        log.info("Order created with id: {}", savedOrder.getId());

        OrderCreatedEvent event = new OrderCreatedEvent();
        event.setOrderId(savedOrder.getId());
        event.setUserId(savedOrder.getUserId());
        event.setAmount(savedOrder.getAmount());
        event.setDescription(savedOrder.getDescription());

        OrderOutbox outboxEvent = new OrderOutbox();
        outboxEvent.setAggregateId(savedOrder.getId());
        outboxEvent.setEventType("ORDER_CREATED");
        outboxEvent.setPayload(objectMapper.writeValueAsString(event));
        outboxEvent.setStatus(OrderOutbox.Status.PENDING);

        outboxRepository.save(outboxEvent);
        log.info("Outbox event created for order: {}", savedOrder.getId());

        return savedOrder;
    }

    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
    }

    @Transactional
    public void updateOrderStatus(UUID orderId, boolean paymentSuccess) {
        log.info("Updating order {} status. Payment success: {}", orderId, paymentSuccess);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        if (paymentSuccess) {
            order.setStatus(Order.OrderStatus.FINISHED);
        } else {
            order.setStatus(Order.OrderStatus.CANCELLED);
        }

        orderRepository.save(order);
        log.info("Order {} status updated to {}", orderId, order.getStatus());
    }
}