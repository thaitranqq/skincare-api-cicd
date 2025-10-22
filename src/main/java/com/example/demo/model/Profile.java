package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "profiles")
@Data
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId; // PK

    private String skinType;

    @Column(columnDefinition = "text")
    private String concerns; // JSON array string

    @Column(columnDefinition = "text")
    private String allergies; // JSON array string

    private Boolean pregnant;

    @Column(columnDefinition = "text")
    private String goals; // JSON array string

    @Column(columnDefinition = "text")
    private String lifestyleJson;
}
