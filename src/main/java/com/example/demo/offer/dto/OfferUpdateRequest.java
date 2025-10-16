package com.example.demo.offer.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OfferUpdateRequest {
    private BigDecimal price;
    private String url;
    private Long retailerId;
}
