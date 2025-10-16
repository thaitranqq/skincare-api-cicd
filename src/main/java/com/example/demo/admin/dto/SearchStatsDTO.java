package com.example.demo.admin.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SearchStatsDTO {
    private List<Map<String, Object>> barcodeScans;
    private List<Map<String, Object>> keywordSearches;
}
