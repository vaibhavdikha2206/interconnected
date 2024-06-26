package com.simplified.interconnected.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentLinkRequestDto {
    private String customerName;
    private String customerEmail;
    private long serviceId;
    private long expertId;
    private LocalDateTime serviceTimeslot;
}
