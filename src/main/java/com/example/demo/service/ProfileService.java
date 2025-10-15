package com.example.demo.service;

import com.example.demo.personalization.dto.ProfileDTO;

import java.util.Map;

public interface ProfileService {
    ProfileDTO getProfile(Long userId);
    ProfileDTO updateProfile(Long userId, ProfileDTO body);
    Map<String, Object> getPrefs(Long userId);
    void updatePrefs(Long userId, Map<String, Object> body);
    Map<String, Object> checkProduct(Long userId, String productId, ProfileDTO override);

}
