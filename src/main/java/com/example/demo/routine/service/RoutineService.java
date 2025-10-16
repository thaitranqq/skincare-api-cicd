package com.example.demo.routine.service;

import com.example.demo.routine.dto.RoutineCreateRequest;
import com.example.demo.routine.dto.RoutineDTO;
import com.example.demo.routine.dto.RoutineItemCreateRequest;
import com.example.demo.routine.dto.RoutineItemDTO;
import com.example.demo.routine.dto.RoutineItemUpdateRequest;
import com.example.demo.routine.dto.RoutineUpdateRequest;

import java.util.List;

public interface RoutineService {
    // Routine operations
    List<RoutineDTO> getAllRoutines();
    RoutineDTO getRoutineById(Long id);
    List<RoutineDTO> getRoutinesByUserId(Long userId);
    RoutineDTO createRoutine(RoutineCreateRequest request);
    RoutineDTO updateRoutine(Long id, RoutineUpdateRequest request);
    void deleteRoutine(Long id);

    // Routine Item operations
    List<RoutineItemDTO> getRoutineItemsByRoutineId(Long routineId);
    RoutineItemDTO addRoutineItem(RoutineItemCreateRequest request);
    RoutineItemDTO updateRoutineItem(Long routineId, Long productId, RoutineItemUpdateRequest request);
    void deleteRoutineItem(Long routineId, Long productId);
}
