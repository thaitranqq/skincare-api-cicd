package com.example.demo.controller;

import com.example.demo.personalization.dto.ProfileDTO;
import com.example.demo.service.impl.ProfileServiceImpl;
import com.example.demo.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileServiceImpl profileService;

    @GetMapping
    public ResponseEntity<ProfileDTO> getProfile(@AuthenticationPrincipal CurrentUser currentUser) {
        Long userId = (currentUser != null) ? currentUser.getId() : 1L; // fallback for demo
        return ResponseEntity.ok(profileService.getProfile(userId));
    }

    @PutMapping
    public ResponseEntity<ProfileDTO> updateProfile(@AuthenticationPrincipal CurrentUser currentUser,
                                                    @RequestBody ProfileDTO body) {
        Long userId = (currentUser != null) ? currentUser.getId() : 1L;
        return ResponseEntity.ok(profileService.updateProfile(userId, body));
    }

    @GetMapping("/prefs")
    public ResponseEntity<Map<String, Object>> getPrefs(@AuthenticationPrincipal CurrentUser currentUser) {
        Long userId = (currentUser != null) ? currentUser.getId() : 1L;
        return ResponseEntity.ok(Map.of("prefs", profileService.getPrefs(userId)));
    }

    @PutMapping("/prefs")
    public ResponseEntity<Void> updatePrefs(@AuthenticationPrincipal CurrentUser currentUser,
                                            @RequestBody Map<String, Object> body) {
        Long userId = (currentUser != null) ? currentUser.getId() : 1L;
        profileService.updatePrefs(userId, body);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/check-product/{productId}")
    public ResponseEntity<Map<String, Object>> checkProduct(@AuthenticationPrincipal CurrentUser currentUser,
                                                            @PathVariable String productId,
                                                            @RequestBody(required = false) ProfileDTO override) {
        Long userId = (currentUser != null) ? currentUser.getId() : 1L;
        return ResponseEntity.ok(profileService.checkProduct(userId, productId, override));
    }
}
