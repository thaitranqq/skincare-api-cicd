package com.example.demo.personalization.dto;

import com.example.demo.personalization.model.SkinType;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ProfileDTO {
    private Long userId;
    private SkinType skinType;
    private List<String> concerns;
    private List<String> allergies;
    private Boolean pregnant;
    private List<String> goals;
    private Map<String, Object> lifestyle;
}

