package com.example.demo.feedback.service;

import com.example.demo.feedback.dto.FeedbackCreateRequest;
import com.example.demo.feedback.dto.FeedbackDTO;
import com.example.demo.feedback.dto.FeedbackUpdateRequest;

import java.util.List;

public interface FeedbackService {
    List<FeedbackDTO> getAllFeedback();
    FeedbackDTO getFeedbackById(Long id);
    List<FeedbackDTO> getFeedbackByUserId(Long userId);
    List<FeedbackDTO> getFeedbackByProductId(Long productId);
    FeedbackDTO createFeedback(FeedbackCreateRequest request);
    FeedbackDTO updateFeedback(Long id, FeedbackUpdateRequest request);
    void deleteFeedback(Long id);
}
