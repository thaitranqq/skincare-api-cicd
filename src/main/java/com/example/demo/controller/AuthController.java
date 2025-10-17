package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.service.AuthService;
import com.example.demo.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.lang.Nullable;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> signup(@RequestBody Map<String, Object> body) {
        authService.signup(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Signup successful. Please check your email to verify your account."));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok("Your account has been successfully verified. You can now close this tab and log in.");
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
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMe(@AuthenticationPrincipal @Nullable CurrentUser currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("User not authenticated."));
        }
        Map<String, Object> me = authService.getMe(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.ok(me));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<Void>> updateMe(@RequestBody Map<String, Object> body,
                                                      @AuthenticationPrincipal @Nullable CurrentUser currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("User not authenticated."));
        }
        authService.updateMe(currentUser.getId(), body);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @GetMapping("/debug/principal")
    public ResponseEntity<ApiResponse<Map<String, Object>>> debugPrincipal(@AuthenticationPrincipal @Nullable CurrentUser currentUser) {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        assert currentUser != null;
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
