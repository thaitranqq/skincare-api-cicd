package com.example.demo.schedule.service.impl;

import com.example.demo.exception.InvalidRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Schedule;
import com.example.demo.schedule.dto.ScheduleCreateRequest;
import com.example.demo.schedule.dto.ScheduleDTO;
import com.example.demo.schedule.dto.ScheduleUpdateRequest;
import com.example.demo.schedule.service.ScheduleService;
import com.example.demo.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<ScheduleDTO> getAllSchedules() {
        return scheduleRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public ScheduleDTO getScheduleById(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id: " + id));
        return toDto(schedule);
    }

    @Override
    public List<ScheduleDTO> getSchedulesByUserId(Long userId) {
        return scheduleRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<ScheduleDTO> getSchedulesByProductId(Long productId) {
        return scheduleRepository.findByProductId(productId).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public ScheduleDTO createSchedule(ScheduleCreateRequest request) {
        Long userId = request.getUserId();
        if (userId == null) {
            throw new InvalidRequestException("User ID cannot be null for schedule creation.");
        }
        validateUserExists(userId);

        Long productId = request.getProductId();
        if (productId == null) {
            throw new InvalidRequestException("Product ID cannot be null for schedule creation.");
        }
        validateProductExists(productId);

        Schedule schedule = new Schedule();
        schedule.setUserId(userId);
        schedule.setProductId(productId);
        schedule.setCronExpr(HtmlUtils.htmlEscape(request.getCronExpr()));
        schedule.setChannel(HtmlUtils.htmlEscape(request.getChannel()));

        Schedule savedSchedule = scheduleRepository.save(schedule);
        return toDto(savedSchedule);
    }

    @Override
    public ScheduleDTO updateSchedule(Long id, ScheduleUpdateRequest request) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id: " + id));

        if (request.getCronExpr() != null) {
            schedule.setCronExpr(HtmlUtils.htmlEscape(request.getCronExpr()));
        }
        if (request.getChannel() != null) {
            schedule.setChannel(HtmlUtils.htmlEscape(request.getChannel()));
        }
        if (request.getProductId() != null) {
            validateProductExists(request.getProductId());
            schedule.setProductId(request.getProductId());
        }

        Schedule updatedSchedule = scheduleRepository.save(schedule);
        return toDto(updatedSchedule);
    }

    @Override
    public void deleteSchedule(Long id) {
        if (!scheduleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Schedule not found with id: " + id);
        }
        scheduleRepository.deleteById(id);
    }

    private void validateUserExists(Long userId) {
        try {
            jdbcTemplate.queryForObject("SELECT id FROM users WHERE id = ?", Long.class, userId);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
    }

    private void validateProductExists(Long productId) {
        try {
            jdbcTemplate.queryForObject("SELECT id FROM products WHERE id = ?", Long.class, productId);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }
    }

    private ScheduleDTO toDto(Schedule schedule) {
        ScheduleDTO dto = new ScheduleDTO();
        dto.setId(schedule.getId());
        dto.setUserId(schedule.getUserId());
        dto.setProductId(schedule.getProductId());
        dto.setCronExpr(schedule.getCronExpr());
        dto.setChannel(schedule.getChannel());
        return dto;
    }
}
