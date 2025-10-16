package com.example.demo.alert.service;

import com.example.demo.alert.dto.AlertCreateRequest;
import com.example.demo.alert.dto.AlertDTO;
import com.example.demo.alert.dto.AlertUpdateRequest;

import java.util.List;

public interface AlertService {
    List<AlertDTO> getAllAlerts();
    AlertDTO getAlertById(Long id);
    List<AlertDTO> getAlertsByUserId(Long userId);
    List<AlertDTO> getAlertsByStatus(String status);
    AlertDTO createAlert(AlertCreateRequest request);
    AlertDTO updateAlert(Long id, AlertUpdateRequest request);
    void deleteAlert(Long id);
}
