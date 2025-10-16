package com.example.demo.admin.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class UserTrendDTO {
    private List<Map<String, Object>> bySkinType;
    private List<Map<String, Object>> byAgeRange;
    private List<Map<String, Object>> popularProducts;
}
