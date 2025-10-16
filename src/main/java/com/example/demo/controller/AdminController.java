package com.example.demo.controller;

import com.example.demo.admin.dto.SearchStatsDTO;
import com.example.demo.admin.dto.UserTrendDTO;
import com.example.demo.admin.service.AdminService;
import com.example.demo.common.ApiResponse;
import com.example.demo.feedback.dto.FeedbackDTO;
import com.example.demo.product.dto.ProductDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // --- Reports & Statistics ---

    @GetMapping("/trends/users")
    public ResponseEntity<ApiResponse<UserTrendDTO>> userTrends() {
        UserTrendDTO trends = adminService.getUserTrends();
        return ResponseEntity.ok(ApiResponse.ok(trends));
    }

    @GetMapping("/reports/risky-products")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> riskyProducts() {
        List<ProductDTO> products = adminService.getRiskyProductsReport();
        return ResponseEntity.ok(ApiResponse.ok(products));
    }

    @GetMapping("/stats/search-frequency")
    public ResponseEntity<ApiResponse<SearchStatsDTO>> searchFrequency() {
        SearchStatsDTO stats = adminService.getSearchFrequencyStats();
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }

    // --- Content Management ---

    @GetMapping("/content/reviews")
    public ResponseEntity<ApiResponse<List<FeedbackDTO>>> getReviewsForModeration() {
        List<FeedbackDTO> reviews = adminService.getReviewsForModeration();
        return ResponseEntity.ok(ApiResponse.ok(reviews));
    }

    @PostMapping("/content/reviews/{id}/moderate")
    public ResponseEntity<ApiResponse<Void>> moderateReview(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null || status.isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Status is required."));
        }
        adminService.moderateReview(id, status);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @GetMapping("/content/qa")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getQuestionsForModeration() {
        // Placeholder for Q&A moderation
        return ResponseEntity.ok(ApiResponse.ok(Map.of("questions", new Object[] {})));
    }

    @PostMapping("/content/qa/{id}/answer")
    public ResponseEntity<ApiResponse<Void>> answerQuestion(@PathVariable String id, @RequestBody Map<String, Object> body) {
        // Placeholder for answering questions
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @GetMapping("/content/suggestions")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSuggestions() {
        // Placeholder for content suggestions
        return ResponseEntity.ok(ApiResponse.ok(Map.of("suggestions", new Object[] {})));
    }

    // --- Other Admin Functions ---

    @GetMapping("/events")
    public ResponseEntity<ApiResponse<Map<String, Object>>> events(@RequestParam(required = false) String type) {
        return ResponseEntity.ok(ApiResponse.ok(Map.of("events", new Object[] {})));
    }
}
