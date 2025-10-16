package com.example.demo.rec.controller;

import com.example.demo.rec.dto.RecCreateRequest;
import com.example.demo.rec.dto.RecDTO;
import com.example.demo.rec.dto.RecUpdateRequest;
import com.example.demo.rec.service.RecService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recs")
@RequiredArgsConstructor
public class RecController {

    private final RecService recService;

    @GetMapping
    public ResponseEntity<List<RecDTO>> getAllRecs() {
        List<RecDTO> recs = recService.getAllRecs();
        return ResponseEntity.ok(recs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecDTO> getRecById(@PathVariable Long id) {
        RecDTO rec = recService.getRecById(id);
        return ResponseEntity.ok(rec);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RecDTO>> getRecsByUserId(@PathVariable Long userId) {
        List<RecDTO> recs = recService.getRecsByUserId(userId);
        return ResponseEntity.ok(recs);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<RecDTO>> getRecsByProductId(@PathVariable Long productId) {
        List<RecDTO> recs = recService.getRecsByProductId(productId);
        return ResponseEntity.ok(recs);
    }

    @PostMapping
    public ResponseEntity<RecDTO> createRec(@RequestBody RecCreateRequest request) {
        RecDTO createdRec = recService.createRec(request);
        return new ResponseEntity<>(createdRec, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecDTO> updateRec(@PathVariable Long id, @RequestBody RecUpdateRequest request) {
        RecDTO updatedRec = recService.updateRec(id, request);
        return ResponseEntity.ok(updatedRec);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRec(@PathVariable Long id) {
        recService.deleteRec(id);
        return ResponseEntity.noContent().build();
    }
}
