package com.example.quan_ly_sinh_vien_v2.Service.Auth;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.Auth.ChangePasswordRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Request.Auth.ForgotPasswordRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Request.Auth.LoginRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.AuthResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.UserResponse;
import com.example.quan_ly_sinh_vien_v2.Exception.Auth.AuthenticationFailedException;
import com.example.quan_ly_sinh_vien_v2.Exception.Auth.ChangePasswordFailedException;
import com.example.quan_ly_sinh_vien_v2.Exception.Auth.JwtExpiredException;
import com.example.quan_ly_sinh_vien_v2.Exception.NotFoundException;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.RefreshToken;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.User;
import com.example.quan_ly_sinh_vien_v2.Repository.RefreshTokenRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.UserRepository;
import com.example.quan_ly_sinh_vien_v2.Service.MailService;
import com.example.quan_ly_sinh_vien_v2.Util.PasswordGenerater;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired private JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, MailService mailService, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            String accessToken = jwtService.generateAccessToken(request.getEmail());
            String refreshToken = jwtService.generateRefreshToken(request.getEmail());

            User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new AuthenticationFailedException("Email or password not correct!"));

            Instant expiresAt = Instant.now().plus(7, ChronoUnit.DAYS);

            refreshTokenRepository.save(new RefreshToken(user, refreshToken, expiresAt));

            return new AuthResponse(
                    user.getEmail(),
                    user.getRole(),
                    accessToken,
                    refreshToken
            );

        } catch (BadCredentialsException ex) {
            throw new AuthenticationFailedException("Email or password not correct!");
        }

    }

    @Transactional
    public void logout(String refreshToken) {
        try {
            RefreshToken token = refreshTokenRepository.findByToken(refreshToken);

            token.setRevokedAt(Instant.now());
        } catch (RuntimeException ex) {
            throw new JwtExpiredException("Refresh token not found!");
        }
    }

    public AuthResponse refreshToken(String refreshToken) {
        if (jwtService.isTokenExpired(refreshToken)) {
            throw new JwtExpiredException("Token is expired");
        }

        String email = jwtService.extractUsername(refreshToken);

        RefreshToken token = refreshTokenRepository.findByToken(refreshToken);

        if (token.getRevokedAt() != null) {
            throw new JwtExpiredException("Token is not valid!");
        }

        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found!"));

        String accessToken = jwtService.generateAccessToken(email);

        AuthResponse response = new AuthResponse(email, user.getRole(), accessToken, refreshToken);

        return response;
    }

    @Transactional
    public void forgotPassword(@Valid ForgotPasswordRequest request) {
        if (!userRepository.existsUserByEmail(request.getEmail())) {
            throw new NotFoundException("Email not found!");
        }

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new NotFoundException("User not found!"));

        String newPassword = PasswordGenerater.generate(12);

        mailService.sendEmail(
                user.getEmail(),
                "Yêu cầu thay đổi mật khẩu thành công!",
                "Mật khẩu mới của bạn là: " + newPassword
        );

        user.setPassword(passwordEncoder.encode(newPassword));
    }

    public UserResponse getInfo(String username) {
        User user = userRepository.findByEmail(username).orElseThrow(() -> new NotFoundException("User not found!"));

        return UserResponse.from(user);
    }

    @Transactional
    public void changePassword(String username, @Valid ChangePasswordRequest request) {
        User user = userRepository.findByEmail(username).orElseThrow(() -> new NotFoundException("User not found!"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new ChangePasswordFailedException("Change password fail!");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new ChangePasswordFailedException("Change password fail!");
        }

        if (request.getNewPassword().equals(request.getOldPassword())) {
            throw new ChangePasswordFailedException("Change password fail!");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new ChangePasswordFailedException("Change password fail!");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    }
}
