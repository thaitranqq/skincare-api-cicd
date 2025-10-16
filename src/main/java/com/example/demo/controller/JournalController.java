package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class JournalController {

    @GetMapping("/journal")
    public ResponseEntity<ApiResponse<Map<String, Object>>> listJournal(@RequestParam(required = false) String from,
                                                                         @RequestParam(required = false) String to) {
        return ResponseEntity.ok(ApiResponse.ok(Map.of("entries", new Object[] {})));
    }

    @PostMapping("/journal")
    public ResponseEntity<ApiResponse<Void>> createJournal(@RequestBody Map<String, Object> body) {
        // { date, text_note }
        return ResponseEntity.status(201).body(ApiResponse.ok());
    }

    @PostMapping(value = "/journal/{entryId}/photo")
    public ResponseEntity<ApiResponse<Void>> uploadPhoto(@PathVariable String entryId, @RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.ok());
    }
}
