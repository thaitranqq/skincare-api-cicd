package com.example.demo.event.dto;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class EventDTO {
    private Long id;
    private Long userId;
    private String type;
    private String payloadJson;
    private OffsetDateTime timestamp;
}
