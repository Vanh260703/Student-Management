package com.example.quan_ly_sinh_vien_v2.Controller;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.Auth.ChangePasswordRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Request.Auth.ForgotPasswordRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Request.Auth.LoginRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.APIResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.AuthResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.UserResponse;
import com.example.quan_ly_sinh_vien_v2.Service.Auth.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // [POST] api/v2/auth/login
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        AuthResponse authResponse = authService.login(request);

        // Add refresh token in cookie
        Cookie refreshTokenCookie = new Cookie("refreshToken", authResponse.getRefreshToken());

        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false);
        refreshTokenCookie.setPath("/api/v2/auth");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);

        response.addHeader(
                "Set-Cookie",
                "refreshToken=" + authResponse.getRefreshToken() +
                        "; Max-Age=604800" +
                        "; Path=/api/v2/" +
                        "; HttpOnly" +
                        "; Secure" +
                        "; SameSite=Lax"
        );

        APIResponse result = new APIResponse(
                200,
                "Login success!",
                authResponse
        );

        return ResponseEntity.ok(result);
    }

    // [POST] api/v2/auth/logout
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue("refreshToken") String refreshToken, HttpServletResponse response) {
        authService.logout(refreshToken);

        // Delete refresh token in cookie
        Cookie cookie = new Cookie("refreshToken", "");

        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setPath("/v2/auth");

        response.addCookie(cookie);

        APIResponse result = new APIResponse(
                200,
                "Logout success!",
                null
        );

        return ResponseEntity.ok(result);
    }

    // [POST] api/v2/auth/refresh
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue("refreshToken") String refreshToken) {
        AuthResponse authResponse = authService.refreshToken(refreshToken);

        APIResponse response = new APIResponse(
                200,
                "Refresh token success!",
                authResponse
        );

        return ResponseEntity.ok(response);
    }

    // [POST] api/v2/auth/forgot-password
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);

        APIResponse response = new APIResponse(
                200,
                "Require change password success!",
                null
        );

        return ResponseEntity.ok(response);
    }

    // [GET] api/v2/auth/me
    @GetMapping("/me")
    public ResponseEntity<?> info(@AuthenticationPrincipal UserDetails userDetails) {
        UserResponse user = authService.getInfo(userDetails.getUsername());

        APIResponse response = new APIResponse(
                200,
                "Get info success!",
                user
        );

        return ResponseEntity.ok(response);
    }

    // [PATCH] api/v2/auth/change-password
    @PatchMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(userDetails.getUsername(), request);

        APIResponse response = new APIResponse(
                200,
                "Change password success!",
                null
        );

        return ResponseEntity.ok(response);
    }
}
