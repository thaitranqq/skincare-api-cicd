package com.example.demo.schedule.service;

import com.example.demo.schedule.dto.ScheduleCreateRequest;
import com.example.demo.schedule.dto.ScheduleDTO;
import com.example.demo.schedule.dto.ScheduleUpdateRequest;

import java.util.List;

public interface ScheduleService {
    List<ScheduleDTO> getAllSchedules();
    ScheduleDTO getScheduleById(Long id);
    List<ScheduleDTO> getSchedulesByUserId(Long userId);
    List<ScheduleDTO> getSchedulesByProductId(Long productId);
    ScheduleDTO createSchedule(ScheduleCreateRequest request);
    ScheduleDTO updateSchedule(Long id, ScheduleUpdateRequest request);
    void deleteSchedule(Long id);
}
