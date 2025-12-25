package com.hse.paymentservice.repository;

import com.hse.paymentservice.model.PaymentInbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentInboxRepository extends JpaRepository<PaymentInbox, String> {
    boolean existsByIdempotencyKey(String idempotencyKey);
}