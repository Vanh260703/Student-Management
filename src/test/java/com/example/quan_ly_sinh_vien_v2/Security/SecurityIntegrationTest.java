package com.example.quan_ly_sinh_vien_v2.Security;

import com.example.quan_ly_sinh_vien_v2.Config.SecurityConfig;
import com.example.quan_ly_sinh_vien_v2.Controller.AuthController;
import com.example.quan_ly_sinh_vien_v2.Controller.PaymentController;
import com.example.quan_ly_sinh_vien_v2.Controller.StudentController;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.AuthResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.MomoPaymentResponse;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.PaymentStatus;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.Role;
import com.example.quan_ly_sinh_vien_v2.Service.Auth.AuthService;
import com.example.quan_ly_sinh_vien_v2.Service.MomoService;
import com.example.quan_ly_sinh_vien_v2.Service.StudentService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({AuthController.class, StudentController.class, PaymentController.class})
@Import({SecurityConfig.class, JwtAuthenticationEntryPoint.class, JwtAccessDeniedHandler.class})
class SecurityIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;
    @MockitoBean
    private StudentService studentService;
    @MockitoBean
    private MomoService momoService;
    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;
    @MockitoBean
    private UserDetailsService userDetailsService;

    @BeforeEach
    void setUp() throws Exception {
        doAnswer(invocation -> {
            Object request = invocation.getArgument(0);
            Object response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter((jakarta.servlet.ServletRequest) request, (jakarta.servlet.ServletResponse) response);
            return null;
        }).when(jwtAuthFilter).doFilter(any(), any(), any(FilterChain.class));

        when(userDetailsService.loadUserByUsername(anyString()))
                .thenAnswer(invocation -> User.withUsername(invocation.getArgument(0))
                        .password("secret")
                        .roles("STUDENT")
                        .build());
    }

    @Test
    void loginShouldBePermitAll() throws Exception {
        when(authService.login(any())).thenReturn(new AuthResponse("student@example.com", Role.ROLE_STUDENT, "access", "refresh"));

        mockMvc.perform(post("/api/v2/auth/login")
                        .contentType("application/json")
                        .content("""
                                {"email":"student@example.com","password":"secret123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login success!"));
    }

    @Test
    void studentPaymentsShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v2/student/payments"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Unauthorized"));
    }

    @Test
    @WithMockUser(username = "teacher@example.com", roles = "TEACHER")
    void momoCreateShouldRejectWrongRole() throws Exception {
        mockMvc.perform(post("/api/v2/payments/momo/create/15"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Access Denied"));
    }

    @Test
    @WithMockUser(username = "student@example.com", roles = "STUDENT")
    void momoCreateShouldAllowStudentRole() throws Exception {
        when(momoService.createPaymentUrl("student@example.com", 15L))
                .thenReturn(new MomoPaymentResponse(1L, "ORDER-1", "REQ-1", 1_600_000D, PaymentStatus.PENDING, "https://pay.momo.vn", null, null));

        mockMvc.perform(post("/api/v2/payments/momo/create/15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.payUrl").value("https://pay.momo.vn"));
    }
}
