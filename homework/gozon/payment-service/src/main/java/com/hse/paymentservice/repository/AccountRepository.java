package com.hse.paymentservice.repository;

import com.hse.paymentservice.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    Optional<Account> findByUserId(Long userId);

    @Modifying
    @Query("UPDATE Account a SET a.balance = a.balance - :amount, a.version = a.version + 1 " +
            "WHERE a.userId = :userId AND a.balance >= :amount AND a.version = :expectedVersion")
    int debitAccount(@Param("userId") Long userId,
                     @Param("amount") java.math.BigDecimal amount,
                     @Param("expectedVersion") Long expectedVersion);

    @Modifying
    @Query("UPDATE Account a SET a.balance = a.balance + :amount, a.version = a.version + 1 " +
            "WHERE a.userId = :userId AND a.version = :expectedVersion")
    int creditAccount(@Param("userId") Long userId,
                      @Param("amount") java.math.BigDecimal amount,
                      @Param("expectedVersion") Long expectedVersion);
}