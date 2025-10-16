package com.example.demo.schedule.controller;

import com.example.demo.schedule.dto.ScheduleCreateRequest;
import com.example.demo.schedule.dto.ScheduleDTO;
import com.example.demo.schedule.dto.ScheduleUpdateRequest;
import com.example.demo.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<List<ScheduleDTO>> getAllSchedules() {
        List<ScheduleDTO> schedules = scheduleService.getAllSchedules();
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleDTO> getScheduleById(@PathVariable Long id) {
        ScheduleDTO schedule = scheduleService.getScheduleById(id);
        return ResponseEntity.ok(schedule);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesByUserId(@PathVariable Long userId) {
        List<ScheduleDTO> schedules = scheduleService.getSchedulesByUserId(userId);
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesByProductId(@PathVariable Long productId) {
        List<ScheduleDTO> schedules = scheduleService.getSchedulesByProductId(productId);
        return ResponseEntity.ok(schedules);
    }

    @PostMapping
    public ResponseEntity<ScheduleDTO> createSchedule(@RequestBody ScheduleCreateRequest request) {
        ScheduleDTO createdSchedule = scheduleService.createSchedule(request);
        return new ResponseEntity<>(createdSchedule, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScheduleDTO> updateSchedule(@PathVariable Long id, @RequestBody ScheduleUpdateRequest request) {
        ScheduleDTO updatedSchedule = scheduleService.updateSchedule(id, request);
        return ResponseEntity.ok(updatedSchedule);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }
}
