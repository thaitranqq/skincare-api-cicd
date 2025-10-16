package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "regulatory_labels")
@Data
public class RegulatoryLabel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String region;
    private String code;
    @Column(columnDefinition = "text")
    private String description;
    private String level;
}
