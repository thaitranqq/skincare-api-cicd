package com.example.demo.product.dto;

import lombok.Data;

@Data
public class ProductCreateRequest {
    private String name;
    private String upcEan;
    private String category;
    private String imageUrl;
    private String country;
    private Long brandId; // To link with an existing brand
}
