package com.simplified.interconnected.dto;

import lombok.Data;

@Data
public class OrderResponseDto {
    private String razorpayOrderId;
    private String currency;
    private double amount;
}
