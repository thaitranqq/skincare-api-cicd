package com.example.demo.rec.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
public class RecDTO {
    private Long id;
    private Long userId;
    private Long productId;
    private BigDecimal score;
    private String reasonJson;
    private OffsetDateTime createdAt;
}
