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

    @Autowired
    @Qualifier(value = "db0PaymentRepository")
    private PaymentRepository db0PaymentRepository;

    private final ExecutorService db0InsertPaymentExecutor = Executors.newSingleThreadExecutor();

    @Autowired
    @Qualifier(value = "db1PaymentRepository")
    private PaymentRepository db1PaymentRepository;

    private final ExecutorService db1InsertPaymentExecutor = Executors.newSingleThreadExecutor();

    @Autowired
    @Qualifier(value = "db2PaymentRepository")
    private PaymentRepository db2PaymentRepository;

    private final ExecutorService db2InsertPaymentExecutor = Executors.newSingleThreadExecutor();

    public List<PaymentDto> savePayments(List<PaymentDto> payments) {
        Map<Integer, List<PaymentDto>> groupsOfPayments = payments.stream()
                .collect(Collectors.groupingBy(e -> getDatabaseNodeNumberForSenderId(e.getSenderId())));
        List<Future<List<PaymentDto>>> futures = new ArrayList<>();
        for (Map.Entry<Integer, List<PaymentDto>> entry : groupsOfPayments.entrySet()) {
            Future<List<PaymentDto>> future = submitPaymentListInsertion(entry);
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
        Long sum = paymentRepository.findSum(senderId);
        return new SenderTotalInfoDto(senderId, sum);
    }

    private Future<List<PaymentDto>> submitPaymentListInsertion(Map.Entry<Integer, List<PaymentDto>> entry) {
        int databaseNodeNumber = entry.getKey();
        ExecutorService insertPaymentExecutor = getInsertPaymentExecutor(databaseNodeNumber);
        return insertPaymentExecutor.submit(() -> {
            PaymentRepository paymentRepository = getPaymentRepository(databaseNodeNumber);
            List<PaymentDto> paymentListToSaveDto = entry.getValue();
            List<Payment> paymentListToSave = paymentListToSaveDto.stream() //
                    .map(payment -> new Payment(payment.getSenderId(), payment.getRecipientId(), payment.getAmount())) //
                    .collect(Collectors.toList());
            try {
                paymentRepository.saveAll(paymentListToSave);
            } catch (Exception e) {
                return paymentListToSaveDto;
            }
            return Collections.emptyList();
        });
    }

    private int getDatabaseNodeNumberForSenderId(long senderId) {
        return (int) senderId % 3;
    }

    private ExecutorService getInsertPaymentExecutor(int databaseNodeNumber) {
        if (databaseNodeNumber == 0) {
            return this.db0InsertPaymentExecutor;
        } else if (databaseNodeNumber == 1) {
            return this.db1InsertPaymentExecutor;
        }
        return this.db2InsertPaymentExecutor;
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
        this.db0InsertPaymentExecutor.shutdown();
        this.db1InsertPaymentExecutor.shutdown();
        this.db2InsertPaymentExecutor.shutdown();
    }
}
