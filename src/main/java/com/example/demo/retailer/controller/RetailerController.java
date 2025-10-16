package com.example.demo.retailer.controller;

import com.example.demo.retailer.dto.RetailerCreateRequest;
import com.example.demo.retailer.dto.RetailerDTO;
import com.example.demo.retailer.dto.RetailerUpdateRequest;
import com.example.demo.retailer.service.RetailerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/retailers")
@RequiredArgsConstructor
public class RetailerController {

    private final RetailerService retailerService;

    @GetMapping
    public ResponseEntity<List<RetailerDTO>> getAllRetailers() {
        List<RetailerDTO> retailers = retailerService.getAllRetailers();
        return ResponseEntity.ok(retailers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RetailerDTO> getRetailerById(@PathVariable Long id) {
        RetailerDTO retailer = retailerService.getRetailerById(id);
        return ResponseEntity.ok(retailer);
    }

    @PostMapping
    public ResponseEntity<RetailerDTO> createRetailer(@RequestBody RetailerCreateRequest request) {
        RetailerDTO createdRetailer = retailerService.createRetailer(request);
        return new ResponseEntity<>(createdRetailer, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RetailerDTO> updateRetailer(@PathVariable Long id, @RequestBody RetailerUpdateRequest request) {
        RetailerDTO updatedRetailer = retailerService.updateRetailer(id, request);
        return ResponseEntity.ok(updatedRetailer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRetailer(@PathVariable Long id) {
        retailerService.deleteRetailer(id);
        return ResponseEntity.noContent().build();
    }
}
