package com.example.demo.alert.dto;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class AlertDTO {
    private Long id;
    private Long userId;
    private String type;
    private String payloadJson;
    private String status;
    private OffsetDateTime createdAt;
}
