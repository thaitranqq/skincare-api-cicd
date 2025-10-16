package com.example.demo.event.dto;

import lombok.Data;

@Data
public class EventUpdateRequest {
    private String type;
    private String payloadJson;
}
