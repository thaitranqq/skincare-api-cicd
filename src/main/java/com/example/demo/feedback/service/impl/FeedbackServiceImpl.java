package com.example.demo.feedback.service.impl;

import com.example.demo.model.Feedback;
import com.example.demo.model.Product;
import com.example.demo.feedback.dto.FeedbackCreateRequest;
import com.example.demo.feedback.dto.FeedbackDTO;
import com.example.demo.feedback.dto.FeedbackUpdateRequest;
import com.example.demo.feedback.service.FeedbackService;
import com.example.demo.repository.FeedbackRepository;
import com.example.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final ProductRepository productRepository; // Injected to fetch Product entity

    @Override
    @Transactional(readOnly = true)
    public List<FeedbackDTO> getAllFeedback() {
        return feedbackRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FeedbackDTO getFeedbackById(Long id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found with id: " + id));
        return toDto(feedback);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedbackDTO> getFeedbackByUserId(Long userId) {
        return feedbackRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedbackDTO> getFeedbackByProductId(Long productId) {
        return feedbackRepository.findByProductId(productId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FeedbackDTO createFeedback(FeedbackCreateRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + request.getProductId()));

        Feedback feedback = new Feedback();
        feedback.setUserId(request.getUserId());
        feedback.setProduct(product); // Set the Product entity
        feedback.setRating(request.getRating());
        feedback.setComment(request.getComment()); // Use comment instead of note

        Feedback savedFeedback = feedbackRepository.save(feedback);
        return toDto(savedFeedback);
    }

    @Override
    @Transactional
    public FeedbackDTO updateFeedback(Long id, FeedbackUpdateRequest request) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found with id: " + id));

        if (request.getRating() != null) {
            feedback.setRating(request.getRating());
        }
        if (request.getComment() != null) {
            feedback.setComment(request.getComment()); // Use comment instead of note
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

    private FeedbackDTO toDto(Feedback feedback) {
        FeedbackDTO dto = new FeedbackDTO();
        dto.setId(feedback.getId());
        dto.setUserId(feedback.getUserId());
        if (feedback.getProduct() != null) {
            dto.setProductId(feedback.getProduct().getId());
        }
        dto.setRating(feedback.getRating());
        dto.setComment(feedback.getComment()); // Use getComment()
        dto.setCreatedAt(feedback.getCreatedAt());
        return dto;
    }
}
