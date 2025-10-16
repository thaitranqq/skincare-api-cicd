package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class AlertsController {

    @PostMapping("/alerts/check-product/{productId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkProductAlerts(@PathVariable String productId) {
        return ResponseEntity.ok(ApiResponse.ok(Map.of("flags", new String[] {})));
    }
}
