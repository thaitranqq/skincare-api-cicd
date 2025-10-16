package com.example.demo.alert.service.impl;

import com.example.demo.model.Alert;
import com.example.demo.alert.dto.AlertCreateRequest;
import com.example.demo.alert.dto.AlertDTO;
import com.example.demo.alert.dto.AlertUpdateRequest;
import com.example.demo.alert.service.AlertService;
import com.example.demo.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;

    @Override
    public List<AlertDTO> getAllAlerts() {
        return alertRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public AlertDTO getAlertById(Long id) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alert not found with id: " + id));
        return toDto(alert);
    }

    @Override
    public List<AlertDTO> getAlertsByUserId(Long userId) {
        return alertRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AlertDTO> getAlertsByStatus(String status) {
        return alertRepository.findByStatus(status).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public AlertDTO createAlert(AlertCreateRequest request) {
        Alert alert = new Alert();
        alert.setUserId(request.getUserId());
        alert.setType(request.getType());
        alert.setPayloadJson(request.getPayloadJson());
        alert.setStatus(request.getStatus());
        alert.setCreatedAt(OffsetDateTime.now());

        Alert savedAlert = alertRepository.save(alert);
        return toDto(savedAlert);
    }

    @Override
    public AlertDTO updateAlert(Long id, AlertUpdateRequest request) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alert not found with id: " + id));

        if (request.getType() != null) {
            alert.setType(request.getType());
        }
        if (request.getPayloadJson() != null) {
            alert.setPayloadJson(request.getPayloadJson());
        }
        if (request.getStatus() != null) {
            alert.setStatus(request.getStatus());
        }

        Alert updatedAlert = alertRepository.save(alert);
        return toDto(updatedAlert);
    }

    @Override
    public void deleteAlert(Long id) {
        if (!alertRepository.existsById(id)) {
            throw new RuntimeException("Alert not found with id: " + id);
        }
        alertRepository.deleteById(id);
    }

    private AlertDTO toDto(Alert alert) {
        AlertDTO dto = new AlertDTO();
        dto.setId(alert.getId());
        dto.setUserId(alert.getUserId());
        dto.setType(alert.getType());
        dto.setPayloadJson(alert.getPayloadJson());
        dto.setStatus(alert.getStatus());
        dto.setCreatedAt(alert.getCreatedAt());
        return dto;
    }
}
