package com.kinkl.paymentlistservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SenderTotalInfo {
    private int senderId;
    private int totallySent;
}
