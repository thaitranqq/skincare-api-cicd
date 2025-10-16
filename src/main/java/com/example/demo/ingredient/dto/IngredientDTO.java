package com.example.demo.ingredient.dto;

import lombok.Data;

@Data
public class IngredientDTO {
    private Long id;
    private String inciName;
    private String aliasVi;
    private String descriptionVi;
    private String functions;
    private String riskLevel;
    private String bannedIn;
    private String typicalRange;
    private String sources;
}
