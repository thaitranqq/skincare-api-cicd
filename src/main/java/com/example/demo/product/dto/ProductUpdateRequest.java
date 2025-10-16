package com.example.demo.product.dto;

import lombok.Data;

@Data
public class ProductUpdateRequest {
    private String name;
    private String upcEan;
    private String category;
    private String imageUrl;
    private String country;
    private Long brandId; // To update the associated brand
}
