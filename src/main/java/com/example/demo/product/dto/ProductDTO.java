package com.example.demo.product.dto;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private String upcEan;
    private String category;
    private String imageUrl;
    private String country;
    private Long brandId;
    private String brandName;
    // Removed List<ProductIngredient> ingredients to avoid lazy loading issues
    private OffsetDateTime createdAt;
}
