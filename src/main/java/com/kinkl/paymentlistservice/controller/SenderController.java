package com.kinkl.paymentlistservice.controller;

import com.kinkl.paymentlistservice.dto.SenderTotalInfoDto;
import com.kinkl.paymentlistservice.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/senders")
public class SenderController {

    @Autowired
    @Qualifier(value = "db0PaymentRepository")
    private PaymentRepository db0PaymentRepository;

    @Autowired
    @Qualifier(value = "db1PaymentRepository")
    private PaymentRepository db1PaymentRepository;

    @Autowired
    @Qualifier(value = "db2PaymentRepository")
    private PaymentRepository db2PaymentRepository;

    @RequestMapping("/{senderId}")
    public SenderTotalInfoDto getSenderTotalInfo(@PathVariable("senderId") long senderId) {
        long databaseNodeNumber = senderId % 3;
        PaymentRepository paymentRepository;
        if (databaseNodeNumber == 0) {
            paymentRepository = this.db0PaymentRepository;
        } else if (databaseNodeNumber == 1) {
            paymentRepository = this.db1PaymentRepository;
        } else {
            paymentRepository = this.db2PaymentRepository;
        }
        Long sum = paymentRepository.findSum(senderId);
        System.out.println(senderId + " has spent " + sum); // TODO remove this line after testing
        return new SenderTotalInfoDto(senderId, sum);
    }

}
