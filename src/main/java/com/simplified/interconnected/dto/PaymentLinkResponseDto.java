package com.simplified.interconnected.dto;

import lombok.Data;

@Data
public class PaymentLinkResponseDto {
    private String payment_link_id;
    private String payment_link_url;
}
