package com.example.demo.catalog.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "brands")
@Data
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
}

