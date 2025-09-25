package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @GetMapping("/reports/risky-products")
    public ResponseEntity<ApiResponse<Map<String, Object>>> riskyProducts() {
        return ResponseEntity.ok(ApiResponse.ok(Map.of("items", new Object[] {})));
    }

    @GetMapping("/events")
    public ResponseEntity<ApiResponse<Map<String, Object>>> events(@RequestParam(required = false) String type) {
        return ResponseEntity.ok(ApiResponse.ok(Map.of("events", new Object[] {})));
    }

    @PostMapping("/moderate/reviews/{id}")
    public ResponseEntity<ApiResponse<Void>> moderateReview(@PathVariable String id, @RequestBody Map<String, Object> body) {
        // body: { status }
        return ResponseEntity.ok(ApiResponse.ok());
    }
}

