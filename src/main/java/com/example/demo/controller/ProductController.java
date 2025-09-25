package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> searchProducts(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort) {
        return ResponseEntity.ok(Map.of("items", new Object[] {}, "page", page));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProduct(@PathVariable String id) {
        return ResponseEntity.ok(Map.of("id", id, "ingredients", new Object[] {}));
    }

    @GetMapping("/barcode/{code}")
    public ResponseEntity<Map<String, Object>> getByBarcode(@PathVariable String code) {
        return ResponseEntity.ok(Map.of("barcode", code));
    }

    @PostMapping
    public ResponseEntity<Void> createProduct(@RequestBody Map<String, Object> body) {
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProduct(@PathVariable String id, @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok().build();
    }
}

