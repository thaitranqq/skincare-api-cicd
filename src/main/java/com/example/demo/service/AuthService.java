package com.example.demo.service;

import java.util.Map;

public interface AuthService {
    void signup(Map<String, Object> body);
    Map<String, Object> signin(Map<String, Object> body);
    void verifyEmail(String token);
    void requestOtp(Map<String, Object> body);
    void verifyOtp(Map<String, Object> body);
    Map<String, Object> oauthLogin(String provider, Map<String, Object> body);
    Map<String, String> refresh(String refreshToken);
    Map<String, Object> getMe(Long userId);
    void updateMe(Long userId, Map<String, Object> body);

}
