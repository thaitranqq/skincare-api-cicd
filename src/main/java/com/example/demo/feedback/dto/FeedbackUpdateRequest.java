package com.example.demo.feedback.dto;

import lombok.Data;

@Data
public class FeedbackUpdateRequest {
    private Integer rating;
    private String comment;
}
