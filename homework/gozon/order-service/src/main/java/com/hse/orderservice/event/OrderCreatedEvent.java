package com.hse.orderservice.event;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class OrderCreatedEvent {
    private UUID orderId;
    private Long userId;
    private BigDecimal amount;
    private String description;
}