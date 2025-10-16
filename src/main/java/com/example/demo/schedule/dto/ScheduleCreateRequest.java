package com.example.demo.schedule.dto;

import lombok.Data;

@Data
public class ScheduleCreateRequest {
    private Long userId;
    private Long productId;
    private String cronExpr;
    private String channel;
}
