package com.example.demo.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class ProductIngredientId implements Serializable {
    private Long productId;
    private Long ingredientId;
}

