package com.example.demo.feedback.service.impl;

import com.example.demo.model.Feedback;
import com.example.demo.feedback.dto.FeedbackCreateRequest;
import com.example.demo.feedback.dto.FeedbackDTO;
import com.example.demo.feedback.dto.FeedbackUpdateRequest;
import com.example.demo.feedback.service.FeedbackService;
import com.example.demo.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;

    @Override
    public List<FeedbackDTO> getAllFeedback() {
        return feedbackRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public FeedbackDTO getFeedbackById(Long id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found with id: " + id));
        return toDto(feedback);
    }

    @Override
    public List<FeedbackDTO> getFeedbackByUserId(Long userId) {
        return feedbackRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<FeedbackDTO> getFeedbackByProductId(Long productId) {
        return feedbackRepository.findByProductId(productId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public FeedbackDTO createFeedback(FeedbackCreateRequest request) {
        Feedback feedback = new Feedback();
        feedback.setUserId(request.getUserId());
        feedback.setProductId(request.getProductId());
        feedback.setRating(request.getRating());
        feedback.setReactionTags(request.getReactionTags());
        feedback.setNote(request.getNote());

        Feedback savedFeedback = feedbackRepository.save(feedback);
        return toDto(savedFeedback);
    }

    @Override
    public FeedbackDTO updateFeedback(Long id, FeedbackUpdateRequest request) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found with id: " + id));

        if (request.getRating() != null) {
            feedback.setRating(request.getRating());
        }
        if (request.getReactionTags() != null) {
            feedback.setReactionTags(request.getReactionTags());
        }
        if (request.getNote() != null) {
            feedback.setNote(request.getNote());
        }

        Feedback updatedFeedback = feedbackRepository.save(feedback);
        return toDto(updatedFeedback);
    }

    @Override
    public void deleteFeedback(Long id) {
        if (!feedbackRepository.existsById(id)) {
            throw new RuntimeException("Feedback not found with id: " + id);
        }
        feedbackRepository.deleteById(id);
    }

    // Simple DTO mapping method.
    private FeedbackDTO toDto(Feedback feedback) {
        FeedbackDTO dto = new FeedbackDTO();
        dto.setId(feedback.getId());
        dto.setUserId(feedback.getUserId());
        dto.setProductId(feedback.getProductId());
        dto.setRating(feedback.getRating());
        dto.setReactionTags(feedback.getReactionTags());
        dto.setNote(feedback.getNote());
        dto.setCreatedAt(feedback.getCreatedAt());
        return dto;
    }
}
