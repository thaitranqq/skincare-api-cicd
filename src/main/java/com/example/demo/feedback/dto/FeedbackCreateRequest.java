package com.example.demo.feedback.dto;

import lombok.Data;

@Data
public class FeedbackCreateRequest {
    private Long userId;
    private Long productId;
    private Integer rating;
    private String comment;
}
