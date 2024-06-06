package com.simplified.interconnected.dto;

import lombok.Data;

@Data
public class PaymentLinkRequestDto {
    private int amount;
    private String customerName;
    private String customerEmail;
}
