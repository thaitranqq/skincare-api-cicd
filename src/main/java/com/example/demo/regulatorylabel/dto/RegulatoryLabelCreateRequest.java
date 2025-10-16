package com.example.demo.regulatorylabel.dto;

import lombok.Data;

@Data
public class RegulatoryLabelCreateRequest {
    private String region;
    private String code;
    private String description;
    private String level;
}
