package com.example.demo.rec.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RecCreateRequest {
    private Long userId;
    private Long productId;
    private BigDecimal score;
    private String reasonJson;
}
