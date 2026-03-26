package com.example.quan_ly_sinh_vien_v2.Controller;

import com.example.quan_ly_sinh_vien_v2.DTO.Response.MomoCallbackResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.MomoPaymentResponse;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.PaymentStatus;
import com.example.quan_ly_sinh_vien_v2.Security.JwtAccessDeniedHandler;
import com.example.quan_ly_sinh_vien_v2.Security.JwtAuthFilter;
import com.example.quan_ly_sinh_vien_v2.Security.JwtAuthenticationEntryPoint;
import com.example.quan_ly_sinh_vien_v2.Service.MomoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MomoService momoService;
    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;
    @MockitoBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @MockitoBean
    private JwtAccessDeniedHandler jwtAccessDeniedHandler;
    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser(username = "student@example.com", roles = "STUDENT")
    void createMomoPaymentShouldReturnPayUrl() throws Exception {
        MomoPaymentResponse response = new MomoPaymentResponse(
                1L, "ORDER-1", "REQ-1", 1_600_000D, PaymentStatus.PENDING,
                "https://pay.momo.vn", null, null
        );

        when(momoService.createPaymentUrl("student@example.com", 15L)).thenReturn(response);

        mockMvc.perform(post("/api/v2/payments/momo/create/15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Create MoMo payment success!"))
                .andExpect(jsonPath("$.result.payUrl").value("https://pay.momo.vn"));
    }

    @Test
    void handleIpnShouldReturnStatusFromService() throws Exception {
        when(momoService.handleIpn(anyMap())).thenReturn(HttpStatus.NO_CONTENT);

        mockMvc.perform(post("/api/v2/payments/momo/ipn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderId\":\"ORDER-1\"}"))
                .andExpect(status().isNoContent());
    }

    @Test
    void handleReturnShouldWrapCallbackResponse() throws Exception {
        MomoCallbackResponse response = new MomoCallbackResponse(
                "ORDER-1", "REQ-1", 123L, PaymentStatus.SUCCESS, 0, "Success", true, true
        );
        when(momoService.handleReturn(anyMap())).thenReturn(response);

        mockMvc.perform(get("/api/v2/payments/momo/return").param("orderId", "ORDER-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.result.paymentStatus").value("SUCCESS"));
    }
}
