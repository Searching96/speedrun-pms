package org.f3.postalmanagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.f3.postalmanagement.dto.request.auth.CustomerRegisterRequest;
import org.f3.postalmanagement.dto.request.auth.LoginRequest;
import org.f3.postalmanagement.dto.response.auth.AuthResponse;
import org.f3.postalmanagement.entity.ApiResponse;
import org.f3.postalmanagement.service.IAuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication API for login and register")
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Login with username and password")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse authResponse = authService.login(request.getUsername(), request.getPassword());
        
        ResponseCookie cookie = ResponseCookie.from("accessToken", authResponse.getToken())
                .httpOnly(true)
                .secure(false) // Set to true in production with HTTPS
                .path("/")
                .maxAge(86400) // 24 hours
                .sameSite("Lax")
                .build();

        // Still return the data but the token is now also in a secure cookie
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.<AuthResponse>builder()
                        .success(true)
                        .message("Login successful")
                        .data(authResponse)
                        .build()
                );
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Logout and clear authentication cookie")
    public ResponseEntity<ApiResponse<Void>> logout() {
        ResponseCookie cookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.<Void>builder()
                        .success(true)
                        .message("Logout successful")
                        .build()
                );
    }

    @PostMapping("/register")
    @Operation(summary = "Register", description = "Register new customer account")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody CustomerRegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Registration successful")
                        .build()
        );
    }
}
