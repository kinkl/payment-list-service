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
import java.util.Map;
import java.util.stream.Collectors;

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
        Map<Integer, List<PaymentDto>> groupsOfPayments = payments.stream()
                .collect(Collectors.groupingBy(e -> getDatabaseNodeNumberForSenderId(e.getSenderId())));
        for (Map.Entry<Integer, List<PaymentDto>> entry : groupsOfPayments.entrySet()) {
            PaymentRepository paymentRepository = getPaymentRepository(entry.getKey());
            List<PaymentDto> paymentListToSaveDto = entry.getValue();
            List<Payment> paymentListToSave = paymentListToSaveDto.stream() //
                    .map(payment -> new Payment(payment.getSenderId(), payment.getRecipientId(), payment.getAmount())) //
                    .collect(Collectors.toList());
            try {
                paymentRepository.saveAll(paymentListToSave);
            } catch (Exception e) {
                unhandledPayments.addAll(paymentListToSaveDto);
            }
        }
        return unhandledPayments;
    }

    public SenderTotalInfoDto getSenderTotalInfo(long senderId) {
        PaymentRepository paymentRepository = getPaymentRepository(getDatabaseNodeNumberForSenderId(senderId));
        Long sum = paymentRepository.findSum(senderId);
        return new SenderTotalInfoDto(senderId, sum);
    }

    private int getDatabaseNodeNumberForSenderId(long senderId) {
        return (int) senderId % 3;
    }

    private PaymentRepository getPaymentRepository(int databaseNodeNumber) {
        if (databaseNodeNumber == 0) {
            return this.db0PaymentRepository;
        } else if (databaseNodeNumber == 1) {
            return this.db1PaymentRepository;
        }
        return this.db2PaymentRepository;
    }
}
