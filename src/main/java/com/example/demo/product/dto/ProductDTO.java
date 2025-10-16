package com.example.demo.product.dto;

import com.example.demo.model.Brand;
import com.example.demo.model.ProductIngredient;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private String upcEan;
    private String category;
    private String imageUrl;
    private String country;
    private Brand brand;
    private List<ProductIngredient> ingredients;
    private OffsetDateTime createdAt;
}
