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
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.Role;
import com.example.quan_ly_sinh_vien_v2.Repository.RefreshTokenRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.UserRepository;
import com.example.quan_ly_sinh_vien_v2.Service.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private MailService mailService;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("student@example.com");
        user.setPassword("encoded-old-password");
        user.setRole(Role.ROLE_STUDENT);

        ReflectionTestUtils.setField(authService, "authenticationManager", authenticationManager);
        ReflectionTestUtils.setField(authService, "jwtService", jwtService);
    }

    @Test
    void loginShouldReturnTokensAndPersistRefreshToken() {
        LoginRequest request = new LoginRequest();
        ReflectionTestUtils.setField(request, "email", "student@example.com");
        ReflectionTestUtils.setField(request, "password", "secret");

        when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken("student@example.com")).thenReturn("access-token");
        when(jwtService.generateRefreshToken("student@example.com")).thenReturn("refresh-token");

        AuthResponse response = authService.login(request);

        assertEquals("student@example.com", response.getEmail());
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(captor.capture());
        assertEquals("refresh-token", captor.getValue().getToken());
        assertEquals(user, captor.getValue().getUser());
    }

    @Test
    void loginShouldThrowWhenCredentialsInvalid() {
        LoginRequest request = new LoginRequest();
        ReflectionTestUtils.setField(request, "email", "student@example.com");
        ReflectionTestUtils.setField(request, "password", "wrong");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("bad credentials"));

        assertThrows(AuthenticationFailedException.class, () -> authService.login(request));
        verify(refreshTokenRepository, never()).save(any());
    }

    @Test
    void refreshTokenShouldReturnNewAccessTokenWhenTokenValid() {
        RefreshToken refreshToken = new RefreshToken(user, "refresh-token", Instant.now().plusSeconds(3600));

        when(jwtService.isTokenExpired("refresh-token")).thenReturn(false);
        when(jwtService.extractUsername("refresh-token")).thenReturn("student@example.com");
        when(refreshTokenRepository.findByToken("refresh-token")).thenReturn(refreshToken);
        when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken("student@example.com")).thenReturn("new-access-token");

        AuthResponse response = authService.refreshToken("refresh-token");

        assertEquals("new-access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
    }

    @Test
    void refreshTokenShouldRejectRevokedToken() {
        RefreshToken refreshToken = new RefreshToken(user, "refresh-token", Instant.now().plusSeconds(3600));
        refreshToken.setRevokedAt(Instant.now());

        when(jwtService.isTokenExpired("refresh-token")).thenReturn(false);
        when(jwtService.extractUsername("refresh-token")).thenReturn("student@example.com");
        when(refreshTokenRepository.findByToken("refresh-token")).thenReturn(refreshToken);

        assertThrows(JwtExpiredException.class, () -> authService.refreshToken("refresh-token"));
    }

    @Test
    void forgotPasswordShouldSendMailAndUpdatePassword() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        ReflectionTestUtils.setField(request, "email", "student@example.com");

        when(userRepository.existsUserByEmail("student@example.com")).thenReturn(true);
        when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(any())).thenReturn("new-encoded-password");

        authService.forgotPassword(request);

        verify(mailService).sendEmail(eq("student@example.com"), contains("mật khẩu"), any());
        assertEquals("new-encoded-password", user.getPassword());
    }

    @Test
    void forgotPasswordShouldThrowWhenEmailMissing() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        ReflectionTestUtils.setField(request, "email", "missing@example.com");

        when(userRepository.existsUserByEmail("missing@example.com")).thenReturn(false);

        assertThrows(NotFoundException.class, () -> authService.forgotPassword(request));
    }

    @Test
    void changePasswordShouldUpdatePasswordWhenRequestValid() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        ReflectionTestUtils.setField(request, "oldPassword", "old-password");
        ReflectionTestUtils.setField(request, "newPassword", "new-password");
        ReflectionTestUtils.setField(request, "confirmPassword", "new-password");

        when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old-password", "encoded-old-password")).thenReturn(true);
        when(passwordEncoder.matches("new-password", "encoded-old-password")).thenReturn(false);
        when(passwordEncoder.encode("new-password")).thenReturn("encoded-new-password");

        authService.changePassword("student@example.com", request);

        assertEquals("encoded-new-password", user.getPassword());
    }

    @Test
    void changePasswordShouldRejectWrongOldPassword() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        ReflectionTestUtils.setField(request, "oldPassword", "wrong-old");
        ReflectionTestUtils.setField(request, "newPassword", "new-password");
        ReflectionTestUtils.setField(request, "confirmPassword", "new-password");

        when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-old", "encoded-old-password")).thenReturn(false);

        assertThrows(ChangePasswordFailedException.class, () -> authService.changePassword("student@example.com", request));
    }

    @Test
    void getInfoShouldMapUserResponse() {
        when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(user));

        UserResponse response = authService.getInfo("student@example.com");

        assertNotNull(response);
        assertEquals("student@example.com", response.getEmail());
        assertEquals(Role.ROLE_STUDENT, response.getRole());
    }
}
