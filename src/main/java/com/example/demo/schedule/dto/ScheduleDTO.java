package com.example.demo.schedule.dto;

import lombok.Data;

@Data
public class ScheduleDTO {
    private Long id;
    private Long userId;
    private Long productId;
    private String cronExpr;
    private String channel;
}
