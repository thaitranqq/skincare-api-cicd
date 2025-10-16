package com.example.demo.offer.dto;

import com.example.demo.product.dto.ProductDTO;
import com.example.demo.retailer.dto.RetailerDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
public class OfferDTO {
    private Long id;
    private ProductDTO product;
    private RetailerDTO retailer;
    private BigDecimal price;
    private String url;
    private OffsetDateTime updatedAt;
}
