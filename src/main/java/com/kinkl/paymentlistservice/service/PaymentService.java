package com.kinkl.paymentlistservice.service;

import com.kinkl.paymentlistservice.domain.Payment;
import com.kinkl.paymentlistservice.dto.PaymentDto;
import com.kinkl.paymentlistservice.dto.SenderTotalInfoDto;
import com.kinkl.paymentlistservice.repository.PaymentRepository;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
public class PaymentService implements DisposableBean {

    private final ExecutorService threadPool = Executors.newCachedThreadPool();

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
        Map<Integer, List<PaymentDto>> groupsOfPayments = payments.stream()
                .collect(Collectors.groupingBy(e -> getDatabaseNodeNumberForSenderId(e.getSenderId())));
        List<Future<List<PaymentDto>>> futures = new ArrayList<>();
        for (Map.Entry<Integer, List<PaymentDto>> entry : groupsOfPayments.entrySet()) {
            Future<List<PaymentDto>> future = submitPaymentListInsertion(entry.getKey(), entry.getValue());
            futures.add(future);
        }
        List<PaymentDto> unhandledPayments = new ArrayList<>();
        for (Future<List<PaymentDto>> future : futures) {
            try {
                unhandledPayments.addAll(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return unhandledPayments;
    }

    public SenderTotalInfoDto getSenderTotalInfo(long senderId) {
        PaymentRepository paymentRepository = getPaymentRepository(getDatabaseNodeNumberForSenderId(senderId));
        try {
            Long sum = paymentRepository.findSum(senderId);
            return new SenderTotalInfoDto(senderId, sum);
        } catch (Exception e) {
            return new SenderTotalInfoDto(null, null);
        }
    }

    private Future<List<PaymentDto>> submitPaymentListInsertion(Integer databaseNodeNumber, List<PaymentDto> payments) {
        return this.threadPool.submit(() -> {
            PaymentRepository paymentRepository = getPaymentRepository(databaseNodeNumber);
            List<Payment> paymentListToSave = payments.stream() //
                    .map(payment -> new Payment(payment.getSenderId(), payment.getRecipientId(), payment.getAmount())) //
                    .collect(Collectors.toList());
            try {
                paymentRepository.saveAll(paymentListToSave);
            } catch (Exception e) {
                return payments;
            }
            return Collections.emptyList();
        });
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

    @Override
    public void destroy() {
        this.threadPool.shutdown();
    }
}
