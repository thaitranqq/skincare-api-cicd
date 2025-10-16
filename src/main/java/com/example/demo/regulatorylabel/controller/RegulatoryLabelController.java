package com.example.demo.regulatorylabel.controller;

import com.example.demo.regulatorylabel.dto.RegulatoryLabelCreateRequest;
import com.example.demo.regulatorylabel.dto.RegulatoryLabelDTO;
import com.example.demo.regulatorylabel.dto.RegulatoryLabelUpdateRequest;
import com.example.demo.regulatorylabel.service.RegulatoryLabelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/regulatory-labels")
@RequiredArgsConstructor
public class RegulatoryLabelController {

    private final RegulatoryLabelService regulatoryLabelService;

    @GetMapping
    public ResponseEntity<List<RegulatoryLabelDTO>> getAllRegulatoryLabels() {
        List<RegulatoryLabelDTO> labels = regulatoryLabelService.getAllRegulatoryLabels();
        return ResponseEntity.ok(labels);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegulatoryLabelDTO> getRegulatoryLabelById(@PathVariable Long id) {
        RegulatoryLabelDTO label = regulatoryLabelService.getRegulatoryLabelById(id);
        return ResponseEntity.ok(label);
    }

    @PostMapping
    public ResponseEntity<RegulatoryLabelDTO> createRegulatoryLabel(@RequestBody RegulatoryLabelCreateRequest request) {
        RegulatoryLabelDTO createdLabel = regulatoryLabelService.createRegulatoryLabel(request);
        return new ResponseEntity<>(createdLabel, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegulatoryLabelDTO> updateRegulatoryLabel(@PathVariable Long id, @RequestBody RegulatoryLabelUpdateRequest request) {
        RegulatoryLabelDTO updatedLabel = regulatoryLabelService.updateRegulatoryLabel(id, request);
        return ResponseEntity.ok(updatedLabel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegulatoryLabel(@PathVariable Long id) {
        regulatoryLabelService.deleteRegulatoryLabel(id);
        return ResponseEntity.noContent().build();
    }
}
