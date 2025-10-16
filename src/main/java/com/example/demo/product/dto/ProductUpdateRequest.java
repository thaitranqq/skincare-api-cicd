package com.example.demo.product.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProductUpdateRequest {
    private String name;
    private String upcEan;
    private String category;
    private String country;
    private Long brandId; // To update the associated brand
    private MultipartFile image; // New field for image update
    private boolean deleteImage; // New field to indicate if the existing image should be deleted
}
