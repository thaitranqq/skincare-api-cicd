package com.example.demo.routine.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoutineDTO {
    private Long id;
    private Long userId;
    private String title;
    private List<RoutineItemDTO> items;
}
