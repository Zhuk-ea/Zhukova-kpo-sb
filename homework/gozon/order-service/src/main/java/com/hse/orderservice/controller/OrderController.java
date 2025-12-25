package com.hse.orderservice.controller;

import com.hse.orderservice.model.Order;
import com.hse.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(
            @RequestHeader(name = "X-User-Id") Long userId,
            @RequestBody CreateOrderRequest request) {

        log.info("Received request to create order for user: {}", userId);

        try {
            Order order = orderService.createOrder(
                    userId,
                    request.getAmount(),
                    request.getDescription()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(order);

        } catch (Exception e) {
            log.error("Error creating order for user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Order>> getUserOrders(
            @RequestHeader(name = "X-User-Id") Long userId) {

        log.info("Getting orders for user: {}", userId);
        List<Order> orders = orderService.getUserOrders(userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(
            @RequestHeader(name = "X-User-Id") Long userId,
            @PathVariable UUID orderId) {

        log.info("Getting order {} for user: {}", orderId, userId);

        Order order = orderService.getOrderById(orderId);

        if (!order.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(order);
    }

    public static class CreateOrderRequest {
        private BigDecimal amount;
        private String description;

        // Getters and Setters
        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}