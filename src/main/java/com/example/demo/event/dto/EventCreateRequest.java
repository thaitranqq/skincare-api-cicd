package com.example.demo.event.dto;

import lombok.Data;

@Data
public class EventCreateRequest {
    private Long userId;
    private String type;
    private String payloadJson;
}
