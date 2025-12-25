package com.hse.paymentservice.controller;

import com.hse.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(
            @RequestHeader(name = "X-User-Id") Long userId,
            @RequestParam BigDecimal amount) {

        try {
            paymentService.deposit(userId, amount);
            return ResponseEntity.ok("Deposit successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Deposit failed: " + e.getMessage());
        }
    }

    @GetMapping("/balance")
    public ResponseEntity<BigDecimal> getBalance(
            @RequestHeader(name = "X-User-Id") Long userId) {

        BigDecimal balance = paymentService.getBalance(userId);
        return ResponseEntity.ok(balance);
    }
}