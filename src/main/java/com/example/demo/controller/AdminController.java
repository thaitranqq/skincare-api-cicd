package com.example.demo.controller;

import com.example.demo.admin.dto.SearchStatsDTO;
import com.example.demo.admin.dto.UserTrendDTO;
import com.example.demo.admin.service.AdminService;
import com.example.demo.common.ApiResponse;
import com.example.demo.product.dto.ProductDTO;
import com.example.demo.event.service.EventService; // Import EventService
import com.example.demo.event.dto.EventDTO; // Import EventDTO
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
    private final EventService eventService; // Inject EventService

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

    // --- Admin Event Functions ---

    @GetMapping("/events")
    public ResponseEntity<ApiResponse<List<EventDTO>>> getAllEvents() {
        List<EventDTO> events = eventService.getAllEvents();
        return ResponseEntity.ok(ApiResponse.ok(events));
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<ApiResponse<EventDTO>> getEventById(@PathVariable Long id) {
        EventDTO event = eventService.getEventById(id);
        return ResponseEntity.ok(ApiResponse.ok(event));
    }

    @GetMapping("/events/user/{userId}")
    public ResponseEntity<ApiResponse<List<EventDTO>>> getEventsByUserId(@PathVariable Long userId) {
        List<EventDTO> events = eventService.getEventsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.ok(events));
    }

    @GetMapping("/events/type/{type}")
    public ResponseEntity<ApiResponse<List<EventDTO>>> getEventsByType(@PathVariable String type) {
        List<EventDTO> events = eventService.getEventsByType(type);
        return ResponseEntity.ok(ApiResponse.ok(events));
    }
}
