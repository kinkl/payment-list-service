package com.kinkl.paymentlistservice.controller;

import com.kinkl.paymentlistservice.domain.Payment;
import com.kinkl.paymentlistservice.dto.PaymentDto;
import com.kinkl.paymentlistservice.dto.PaymentListDto;
import com.kinkl.paymentlistservice.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    @Qualifier(value = "db0PaymentRepository")
    private PaymentRepository db0PaymentRepository;

    @Autowired
    @Qualifier(value = "db1PaymentRepository")
    private PaymentRepository db1PaymentRepository;

    @Autowired
    @Qualifier(value = "db2PaymentRepository")
    private PaymentRepository db2PaymentRepository;

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public @ResponseBody PaymentListDto savePayments(@RequestBody PaymentListDto payments) {
        List<PaymentDto> unhandledPayments = new ArrayList<>();
        for (PaymentDto payment : payments.getPayments()) {
            System.out.println(payment);
            long databaseNodeNumber = payment.getRecipientId() % 3;
            PaymentRepository paymentRepository;
            if (databaseNodeNumber == 0) {
                paymentRepository = this.db0PaymentRepository;
            } else if (databaseNodeNumber == 1) {
                paymentRepository = this.db1PaymentRepository;
            } else {
                paymentRepository = this.db2PaymentRepository;
            }
            paymentRepository.save(new Payment(payment.getSenderId(), payment.getRecipientId(), payment.getAmount()));
        }
        return new PaymentListDto(unhandledPayments);
    }

}
