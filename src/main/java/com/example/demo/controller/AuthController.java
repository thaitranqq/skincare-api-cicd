package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.identity.service.AuthService;
import com.example.demo.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@RequestBody Map<String, Object> body) {
        authService.signup(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok());
    }

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<Map<String, Object>>> signin(@RequestBody Map<String, Object> body) {
        Map<String, Object> tokens = authService.signin(body);
        return ResponseEntity.ok(ApiResponse.ok(tokens));
    }

    @PostMapping("/otp/request")
    public ResponseEntity<ApiResponse<Void>> otpRequest(@RequestBody Map<String, Object> body) {
        authService.requestOtp(body);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @PostMapping("/otp/verify")
    public ResponseEntity<ApiResponse<Void>> otpVerify(@RequestBody Map<String, Object> body) {
        authService.verifyOtp(body);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @PostMapping("/oauth/{provider}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> oauth(@PathVariable String provider, @RequestBody Map<String, Object> body) {
        Map<String, Object> tokens = authService.oauthLogin(provider, body);
        return ResponseEntity.ok(ApiResponse.ok(tokens));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Map<String, String>>> refresh(@RequestBody Map<String, String> body) {
        Map<String, String> tokens = authService.refresh(body.get("refreshToken"));
        return ResponseEntity.ok(ApiResponse.ok(tokens));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        // TODO: revoke refresh token if persisted
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMe(@AuthenticationPrincipal CurrentUser currentUser) {
        Long userId = (currentUser != null) ? currentUser.getId() : 1L;
        Map<String, Object> me = authService.getMe(userId);
        return ResponseEntity.ok(ApiResponse.ok(me));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<Void>> updateMe(@RequestBody Map<String, Object> body,
                                                      @AuthenticationPrincipal CurrentUser currentUser) {
        Long userId = (currentUser != null) ? currentUser.getId() : 1L;
        authService.updateMe(userId, body);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @GetMapping("/debug/principal")
    public ResponseEntity<ApiResponse<Map<String, Object>>> debugPrincipal(@AuthenticationPrincipal com.example.demo.security.CurrentUser currentUser) {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> info = Map.of(
                "principal", currentUser,
                "authentication", auth != null ? Map.of(
                        "name", auth.getName(),
                        "authenticated", auth.isAuthenticated(),
                        "authorities", auth.getAuthorities()
                ) : null
        );
        return ResponseEntity.ok(ApiResponse.ok(info));
    }
}
