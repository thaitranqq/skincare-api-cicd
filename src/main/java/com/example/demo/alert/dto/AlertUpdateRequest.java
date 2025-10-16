package com.example.demo.alert.dto;

import lombok.Data;

@Data
public class AlertUpdateRequest {
    private String type;
    private String payloadJson;
    private String status;
}
