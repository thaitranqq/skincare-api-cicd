package com.example.demo.schedule.dto;

import lombok.Data;

@Data
public class ScheduleUpdateRequest {
    private String cronExpr;
    private String channel;
    private Long productId;
}
