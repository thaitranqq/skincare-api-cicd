package com.example.demo.rec.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RecUpdateRequest {
    private BigDecimal score;
    private String reasonJson;
}
