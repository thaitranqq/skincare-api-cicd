package com.example.demo.regulatorylabel.dto;

import lombok.Data;

@Data
public class RegulatoryLabelUpdateRequest {
    private String region;
    private String code;
    private String description;
    private String level;
}
