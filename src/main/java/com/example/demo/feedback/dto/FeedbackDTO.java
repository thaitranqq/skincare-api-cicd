package com.example.demo.feedback.dto;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class FeedbackDTO {
    private Long id;
    private Long userId;
    private Long productId;
    private Integer rating;
    private String reactionTags; // JSON string
    private String note;
    private OffsetDateTime createdAt;
}
