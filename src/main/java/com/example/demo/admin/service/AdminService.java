package com.example.demo.admin.service;

import com.example.demo.admin.dto.SearchStatsDTO;
import com.example.demo.admin.dto.UserTrendDTO;
import com.example.demo.feedback.dto.FeedbackDTO;
import com.example.demo.product.dto.ProductDTO;

import java.util.List;

public interface AdminService {

    UserTrendDTO getUserTrends();

    List<ProductDTO> getRiskyProductsReport();

    SearchStatsDTO getSearchFrequencyStats();

    List<FeedbackDTO> getReviewsForModeration();

    void moderateReview(Long reviewId, String status);

    // Add methods for Q&A and Suggestions later
}
