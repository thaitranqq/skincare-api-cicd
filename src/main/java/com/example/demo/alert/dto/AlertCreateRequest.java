package com.example.demo.alert.dto;

import lombok.Data;

@Data
public class AlertCreateRequest {
    private Long userId;
    private String type;
    private String payloadJson;
    private String status;
}
