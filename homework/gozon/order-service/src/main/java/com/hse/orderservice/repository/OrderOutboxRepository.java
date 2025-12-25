package com.hse.orderservice.repository;

import com.hse.orderservice.model.OrderOutbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderOutboxRepository extends JpaRepository<OrderOutbox, UUID> {

    @Query("SELECT o FROM OrderOutbox o WHERE o.status = 'PENDING' AND o.createdAt < :threshold")
    List<OrderOutbox> findPendingEventsOlderThan(LocalDateTime threshold);

    List<OrderOutbox> findByStatus(OrderOutbox.Status status);
}