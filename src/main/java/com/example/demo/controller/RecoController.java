package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/reco")
public class RecoController {

    @PostMapping("/products")
    public ResponseEntity<ApiResponse<Map<String, Object>>> recommendProducts(@RequestBody(required = false) Map<String, Object> body) {
        return ResponseEntity.ok(ApiResponse.ok(Map.of("items", new Object[] {}, "reasons", new Object[] {})));
    }

    @PostMapping("/alternatives/{productId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> alternatives(@PathVariable String productId) {
        return ResponseEntity.ok(ApiResponse.ok(Map.of("items", new Object[] {})));
    }

    @PostMapping("/feedback")
    public ResponseEntity<ApiResponse<Void>> feedback(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(ApiResponse.ok());
    }
}

