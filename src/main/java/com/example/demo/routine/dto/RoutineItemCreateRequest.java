package com.example.demo.routine.dto;

import lombok.Data;

@Data
public class RoutineItemCreateRequest {
    private Long routineId;
    private Long productId;
    private Integer step;
    private String timeOfDay;
}
