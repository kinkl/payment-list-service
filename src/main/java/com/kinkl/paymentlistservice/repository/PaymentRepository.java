package com.kinkl.paymentlistservice.repository;

import com.kinkl.paymentlistservice.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
