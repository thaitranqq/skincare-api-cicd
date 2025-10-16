package com.example.demo.feedback.controller;

import com.example.demo.feedback.dto.FeedbackCreateRequest;
import com.example.demo.feedback.dto.FeedbackDTO;
import com.example.demo.feedback.dto.FeedbackUpdateRequest;
import com.example.demo.feedback.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @GetMapping
    public ResponseEntity<List<FeedbackDTO>> getAllFeedback() {
        List<FeedbackDTO> feedback = feedbackService.getAllFeedback();
        return ResponseEntity.ok(feedback);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeedbackDTO> getFeedbackById(@PathVariable Long id) {
        FeedbackDTO feedback = feedbackService.getFeedbackById(id);
        return ResponseEntity.ok(feedback);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FeedbackDTO>> getFeedbackByUserId(@PathVariable Long userId) {
        List<FeedbackDTO> feedback = feedbackService.getFeedbackByUserId(userId);
        return ResponseEntity.ok(feedback);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<FeedbackDTO>> getFeedbackByProductId(@PathVariable Long productId) {
        List<FeedbackDTO> feedback = feedbackService.getFeedbackByProductId(productId);
        return ResponseEntity.ok(feedback);
    }

    @PostMapping
    public ResponseEntity<FeedbackDTO> createFeedback(@RequestBody FeedbackCreateRequest request) {
        FeedbackDTO createdFeedback = feedbackService.createFeedback(request);
        return new ResponseEntity<>(createdFeedback, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FeedbackDTO> updateFeedback(@PathVariable Long id, @RequestBody FeedbackUpdateRequest request) {
        FeedbackDTO updatedFeedback = feedbackService.updateFeedback(id, request);
        return ResponseEntity.ok(updatedFeedback);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long id) {
        feedbackService.deleteFeedback(id);
        return ResponseEntity.noContent().build();
    }
}
