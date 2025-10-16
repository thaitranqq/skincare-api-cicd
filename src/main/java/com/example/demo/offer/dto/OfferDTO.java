package com.example.demo.offer.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
public class OfferDTO {
    private Long id;
    private Long productId;
    private String productName;
    private Long retailerId;
    private String retailerName;
    private BigDecimal price;
    private String url;
    private OffsetDateTime updatedAt;
}
