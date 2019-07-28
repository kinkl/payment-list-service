package com.kinkl.paymentlistservice.controller;

import com.kinkl.paymentlistservice.dto.PaymentDto;
import com.kinkl.paymentlistservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public @ResponseBody List<PaymentDto> savePayments(@RequestBody List<PaymentDto> payments) {
        return this.paymentService.savePayments(payments);
    }

}
