package com.kinkl.paymentlistservice.repository;

import com.kinkl.paymentlistservice.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("SELECT SUM(amount) FROM Payment p WHERE p.senderId = ?1")
    Long findSum(Long senderId);
}
