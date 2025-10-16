package com.example.demo.regulatorylabel.dto;

import lombok.Data;

@Data
public class RegulatoryLabelDTO {
    private Long id;
    private String region;
    private String code;
    private String description;
    private String level;
}
