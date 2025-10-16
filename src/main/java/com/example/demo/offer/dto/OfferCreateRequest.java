package com.example.demo.offer.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OfferCreateRequest {
    private Long productId;
    private Long retailerId;
    private BigDecimal price;
    private String url;
}
