package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * A simple controller to check the application's status and version.
 * This is used for health checks and deployment verification.
 */
@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    @GetMapping("/version")
    public ResponseEntity<Map<String, String>> getVersion() {
        // This is our "deployment signature". If we can see this, the new code is live.
        Map<String, String> versionInfo = Map.of(
            "status", "OK",
            "version", "2024-10-16.1", // A unique version identifier
            "message", "Deployment verification endpoint is working!"
        );
        return ResponseEntity.ok(versionInfo);
    }
}
