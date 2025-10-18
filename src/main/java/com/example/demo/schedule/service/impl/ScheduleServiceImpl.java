package com.example.demo.schedule.service.impl;

import com.example.demo.model.Schedule;
import com.example.demo.schedule.dto.ScheduleCreateRequest;
import com.example.demo.schedule.dto.ScheduleDTO;
import com.example.demo.schedule.dto.ScheduleUpdateRequest;
import com.example.demo.schedule.service.ScheduleService;
import com.example.demo.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<ScheduleDTO> getAllSchedules() {
        return scheduleRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ScheduleDTO getScheduleById(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found with id: " + id));
        return toDto(schedule);
    }

    @Override
    public List<ScheduleDTO> getSchedulesByUserId(Long userId) {
        return scheduleRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleDTO> getSchedulesByProductId(Long productId) {
        return scheduleRepository.findByProductId(productId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ScheduleDTO createSchedule(ScheduleCreateRequest request) {
        // Validate if the user exists
        Long userId = request.getUserId();
        if (userId != null) {
            try {
                jdbcTemplate.queryForObject("SELECT id FROM users WHERE id = ?", Long.class, userId);
            } catch (EmptyResultDataAccessException e) {
                throw new RuntimeException("User not found with id: " + userId);
            }
        } else {
            throw new RuntimeException("User ID cannot be null for schedule creation.");
        }

        // Validate if the product exists
        Long productId = request.getProductId();
        if (productId != null) {
            try {
                jdbcTemplate.queryForObject("SELECT id FROM products WHERE id = ?", Long.class, productId);
            } catch (EmptyResultDataAccessException e) {
                throw new RuntimeException("Product not found with id: " + productId);
            }
        } else {
            throw new RuntimeException("Product ID cannot be null for schedule creation.");
        }

        Schedule schedule = new Schedule();
        schedule.setUserId(request.getUserId());
        schedule.setProductId(request.getProductId());
        schedule.setCronExpr(HtmlUtils.htmlEscape(request.getCronExpr()));
        schedule.setChannel(HtmlUtils.htmlEscape(request.getChannel()));

        Schedule savedSchedule = scheduleRepository.save(schedule);
        return toDto(savedSchedule);
    }

    @Override
    public ScheduleDTO updateSchedule(Long id, ScheduleUpdateRequest request) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found with id: " + id));

        if (request.getCronExpr() != null) {
            schedule.setCronExpr(HtmlUtils.htmlEscape(request.getCronExpr()));
        }
        if (request.getChannel() != null) {
            schedule.setChannel(HtmlUtils.htmlEscape(request.getChannel()));
        }
        if (request.getProductId() != null) {
            // Validate if the product exists
            Long productId = request.getProductId();
            try {
                jdbcTemplate.queryForObject("SELECT id FROM products WHERE id = ?", Long.class, productId);
            } catch (EmptyResultDataAccessException e) {
                throw new RuntimeException("Product not found with id: " + productId);
            }
            schedule.setProductId(request.getProductId());
        }

        Schedule updatedSchedule = scheduleRepository.save(schedule);
        return toDto(updatedSchedule);
    }

    @Override
    public void deleteSchedule(Long id) {
        if (!scheduleRepository.existsById(id)) {
            throw new RuntimeException("Schedule not found with id: " + id);
        }
        scheduleRepository.deleteById(id);
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