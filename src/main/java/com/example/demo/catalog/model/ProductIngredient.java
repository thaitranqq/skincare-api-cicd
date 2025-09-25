package com.example.demo.catalog.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "product_ingredients")
@Data
public class ProductIngredient {

    @EmbeddedId
    private ProductIngredientId id = new ProductIngredientId();

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @MapsId("ingredientId")
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    private BigDecimal concentrationMin;
    private BigDecimal concentrationMax;
    private Integer positionIndex;
}

