package com.example.demo.routine.controller;

import com.example.demo.routine.dto.RoutineCreateRequest;
import com.example.demo.routine.dto.RoutineDTO;
import com.example.demo.routine.dto.RoutineItemCreateRequest;
import com.example.demo.routine.dto.RoutineItemDTO;
import com.example.demo.routine.dto.RoutineItemUpdateRequest;
import com.example.demo.routine.dto.RoutineUpdateRequest;
import com.example.demo.routine.service.RoutineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RoutineController {

    private final RoutineService routineService;

    // --- Routine Endpoints ---

    @GetMapping("/routines")
    public ResponseEntity<List<RoutineDTO>> getAllRoutines() {
        List<RoutineDTO> routines = routineService.getAllRoutines();
        return ResponseEntity.ok(routines);
    }

    @GetMapping("/routines/{id}")
    public ResponseEntity<RoutineDTO> getRoutineById(@PathVariable Long id) {
        RoutineDTO routine = routineService.getRoutineById(id);
        return ResponseEntity.ok(routine);
    }

    @GetMapping("/users/{userId}/routines")
    public ResponseEntity<List<RoutineDTO>> getRoutinesByUserId(@PathVariable Long userId) {
        List<RoutineDTO> routines = routineService.getRoutinesByUserId(userId);
        return ResponseEntity.ok(routines);
    }

    @PostMapping("/routines")
    public ResponseEntity<RoutineDTO> createRoutine(@RequestBody RoutineCreateRequest request) {
        RoutineDTO createdRoutine = routineService.createRoutine(request);
        return new ResponseEntity<>(createdRoutine, HttpStatus.CREATED);
    }

    @PutMapping("/routines/{id}")
    public ResponseEntity<RoutineDTO> updateRoutine(@PathVariable Long id, @RequestBody RoutineUpdateRequest request) {
        RoutineDTO updatedRoutine = routineService.updateRoutine(id, request);
        return ResponseEntity.ok(updatedRoutine);
    }

    @DeleteMapping("/routines/{id}")
    public ResponseEntity<Void> deleteRoutine(@PathVariable Long id) {
        routineService.deleteRoutine(id);
        return ResponseEntity.noContent().build();
    }

    // --- Routine Item Endpoints ---

    @GetMapping("/routines/{routineId}/items")
    public ResponseEntity<List<RoutineItemDTO>> getRoutineItemsByRoutineId(@PathVariable Long routineId) {
        List<RoutineItemDTO> items = routineService.getRoutineItemsByRoutineId(routineId);
        return ResponseEntity.ok(items);
    }

    @PostMapping("/routines/{routineId}/items")
    public ResponseEntity<RoutineItemDTO> addRoutineItem(@PathVariable Long routineId, @RequestBody RoutineItemCreateRequest request) {
        // Ensure the routineId in the path matches the request body if present, or set it
        if (request.getRoutineId() == null) {
            request.setRoutineId(routineId);
        } else if (!request.getRoutineId().equals(routineId)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Mismatch
        }
        RoutineItemDTO addedItem = routineService.addRoutineItem(request);
        return new ResponseEntity<>(addedItem, HttpStatus.CREATED);
    }

    @PutMapping("/routines/{routineId}/items/{productId}")
    public ResponseEntity<RoutineItemDTO> updateRoutineItem(
            @PathVariable Long routineId,
            @PathVariable Long productId,
            @RequestBody RoutineItemUpdateRequest request) {
        RoutineItemDTO updatedItem = routineService.updateRoutineItem(routineId, productId, request);
        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/routines/{routineId}/items/{productId}")
    public ResponseEntity<Void> deleteRoutineItem(
            @PathVariable Long routineId,
            @PathVariable Long productId) {
        routineService.deleteRoutineItem(routineId, productId);
        return ResponseEntity.noContent().build();
    }
}
