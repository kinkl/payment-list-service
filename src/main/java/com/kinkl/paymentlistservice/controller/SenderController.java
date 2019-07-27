package com.kinkl.paymentlistservice.controller;

import com.kinkl.paymentlistservice.dto.SenderTotalInfoDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/senders")
public class SenderController {

    @RequestMapping("/{senderId}")
    public SenderTotalInfoDto getSenderTotalInfo(@PathVariable("senderId") int senderId) {
        return new SenderTotalInfoDto(senderId, 500);
    }

}
