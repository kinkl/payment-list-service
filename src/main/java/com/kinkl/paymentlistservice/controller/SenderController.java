package com.kinkl.paymentlistservice.controller;

import com.kinkl.paymentlistservice.model.SenderTotalInfo;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/senders")
public class SenderController {

    @RequestMapping("/{senderId}")
    public SenderTotalInfo getSenderTotalInfo(@PathVariable("senderId") int senderId) {
        return new SenderTotalInfo(senderId, 500);
    }

}
