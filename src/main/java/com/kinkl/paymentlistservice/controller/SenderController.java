package com.kinkl.paymentlistservice.controller;

import com.kinkl.paymentlistservice.dto.SenderTotalInfoDto;
import com.kinkl.paymentlistservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/senders")
public class SenderController {

    @Autowired
    private PaymentService paymentService;

    @RequestMapping("/{senderId}")
    public @ResponseBody SenderTotalInfoDto getSenderTotalInfo(@PathVariable("senderId") long senderId) {
        return this.paymentService.getSenderTotalInfo(senderId);
    }

}
