package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ingredients")
@Data
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String inciName;
    private String aliasVi;
    @Column(columnDefinition = "text")
    private String descriptionVi;
    @Column(columnDefinition = "text")
    private String functions; // JSONB string
    private String riskLevel; // enum string
    @Column(columnDefinition = "text")
    private String bannedIn;
    @Column(columnDefinition = "text")
    private String typicalRange;
    @Column(columnDefinition = "text")
    private String sources;
}
