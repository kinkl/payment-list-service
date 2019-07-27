package com.kinkl.paymentlistservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SenderTotalInfoDto {
    private int senderId;
    private int totallySent;
}
