package com.simplified.interconnected.dto;

import com.simplified.interconnected.models.OrderEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentLinkRequestDto {
    private int amount;
    private String customerName;
    private String customerEmail;
    private long orderId;
    private LocalDateTime orderTimestamp;
}
