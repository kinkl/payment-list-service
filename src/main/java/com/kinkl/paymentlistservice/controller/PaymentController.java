package com.kinkl.paymentlistservice.controller;

import com.kinkl.paymentlistservice.dto.PaymentDto;
import com.kinkl.paymentlistservice.dto.PaymentListDto;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public @ResponseBody PaymentListDto savePayments(@RequestBody PaymentListDto payments) {
        List<PaymentDto> unhandledPayments = new ArrayList<>();
        for (PaymentDto payment : payments.getPayments()) {
            System.out.println(payment);
            if (payment.getRecipientId() % 2 == 0) {
                unhandledPayments.add(payment);
            }
        }
        return new PaymentListDto(unhandledPayments);
    }

}
