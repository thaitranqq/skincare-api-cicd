package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/ingredients")
public class IngredientController {

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> listIngredients(@RequestParam(required = false) String filter) {
        return ResponseEntity.ok(ApiResponse.ok(Map.of("items", new Object[] {})));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getIngredient(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(Map.of("id", id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createIngredient(@RequestBody Map<String, Object> body) {
        return ResponseEntity.status(201).body(ApiResponse.ok());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateIngredient(@PathVariable String id, @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(ApiResponse.ok());
    }
}

