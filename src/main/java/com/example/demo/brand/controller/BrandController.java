package com.example.demo.brand.controller;

import com.example.demo.brand.dto.BrandCreateRequest;
import com.example.demo.brand.dto.BrandDTO;
import com.example.demo.brand.dto.BrandUpdateRequest;
import com.example.demo.brand.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @GetMapping
    public ResponseEntity<List<BrandDTO>> getAllBrands() {
        List<BrandDTO> brands = brandService.getAllBrands();
        return ResponseEntity.ok(brands);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandDTO> getBrandById(@PathVariable Long id) {
        BrandDTO brand = brandService.getBrandById(id);
        return ResponseEntity.ok(brand);
    }

    @PostMapping
    public ResponseEntity<BrandDTO> createBrand(@RequestBody BrandCreateRequest request) {
        BrandDTO createdBrand = brandService.createBrand(request);
        return new ResponseEntity<>(createdBrand, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BrandDTO> updateBrand(@PathVariable Long id, @RequestBody BrandUpdateRequest request) {
        BrandDTO updatedBrand = brandService.updateBrand(id, request);
        return ResponseEntity.ok(updatedBrand);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }
}
