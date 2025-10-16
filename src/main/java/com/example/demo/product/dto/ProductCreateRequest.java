package com.example.demo.product.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProductCreateRequest {
    private String name;
    private String upcEan;
    private String category;
    private String country;
    private Long brandId; // To link with an existing brand
    private MultipartFile image; // New field for image upload
}
