package com.example.demo.alert.controller;

import com.example.demo.alert.dto.AlertCreateRequest;
import com.example.demo.alert.dto.AlertDTO;
import com.example.demo.alert.dto.AlertUpdateRequest;
import com.example.demo.alert.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @GetMapping
    public ResponseEntity<List<AlertDTO>> getAllAlerts() {
        List<AlertDTO> alerts = alertService.getAllAlerts();
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertDTO> getAlertById(@PathVariable Long id) {
        AlertDTO alert = alertService.getAlertById(id);
        return ResponseEntity.ok(alert);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AlertDTO>> getAlertsByUserId(@PathVariable Long userId) {
        List<AlertDTO> alerts = alertService.getAlertsByUserId(userId);
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<AlertDTO>> getAlertsByStatus(@PathVariable String status) {
        List<AlertDTO> alerts = alertService.getAlertsByStatus(status);
        return ResponseEntity.ok(alerts);
    }

    @PostMapping
    public ResponseEntity<AlertDTO> createAlert(@RequestBody AlertCreateRequest request) {
        AlertDTO createdAlert = alertService.createAlert(request);
        return new ResponseEntity<>(createdAlert, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlertDTO> updateAlert(@PathVariable Long id, @RequestBody AlertUpdateRequest request) {
        AlertDTO updatedAlert = alertService.updateAlert(id, request);
        return ResponseEntity.ok(updatedAlert);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlert(@PathVariable Long id) {
        alertService.deleteAlert(id);
        return ResponseEntity.noContent().build();
    }
}
