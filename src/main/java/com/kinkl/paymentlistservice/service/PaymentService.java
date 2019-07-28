package com.kinkl.paymentlistservice.service;

import com.kinkl.paymentlistservice.domain.Payment;
import com.kinkl.paymentlistservice.dto.PaymentDto;
import com.kinkl.paymentlistservice.dto.SenderTotalInfoDto;
import com.kinkl.paymentlistservice.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    @Qualifier(value = "db0PaymentRepository")
    private PaymentRepository db0PaymentRepository;

    @Autowired
    @Qualifier(value = "db1PaymentRepository")
    private PaymentRepository db1PaymentRepository;

    @Autowired
    @Qualifier(value = "db2PaymentRepository")
    private PaymentRepository db2PaymentRepository;

    public List<PaymentDto> savePayments(List<PaymentDto> payments) {
        List<PaymentDto> unhandledPayments = new ArrayList<>();
        for (PaymentDto payment : payments) {
            PaymentRepository paymentRepository = getPaymentRepository(payment.getSenderId());
            paymentRepository.save(new Payment(payment.getSenderId(), payment.getRecipientId(), payment.getAmount()));
        }
        return unhandledPayments;
    }

    public SenderTotalInfoDto getSenderTotalInfo(long senderId) {
        PaymentRepository paymentRepository = getPaymentRepository(senderId);
        Long sum = paymentRepository.findSum(senderId);
        return new SenderTotalInfoDto(senderId, sum);
    }

    private PaymentRepository getPaymentRepository(long senderId) {
        long databaseNodeNumber = senderId % 3;
        if (databaseNodeNumber == 0) {
            return this.db0PaymentRepository;
        } else if (databaseNodeNumber == 1) {
            return this.db1PaymentRepository;
        }
        return this.db2PaymentRepository;
    }
}
