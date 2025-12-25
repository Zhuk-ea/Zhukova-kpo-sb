package com.hse.orderservice.event;

import lombok.Data;
import java.util.UUID;

@Data
public class PaymentProcessedEvent {
    private UUID orderId;
    private Long userId;
    private boolean success;
    private String failureReason;
}