package com.example.demo.routine.dto;

import lombok.Data;

@Data
public class RoutineItemUpdateRequest {
    private Integer step;
    private String timeOfDay;
}
