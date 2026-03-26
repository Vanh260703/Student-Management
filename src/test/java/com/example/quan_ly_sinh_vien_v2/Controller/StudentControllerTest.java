package com.example.quan_ly_sinh_vien_v2.Controller;

import com.example.quan_ly_sinh_vien_v2.DTO.Response.StudentPaymentHistoryResponse;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.PaymentMethod;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.PaymentStatus;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.TuitionStatus;
import com.example.quan_ly_sinh_vien_v2.Security.JwtAccessDeniedHandler;
import com.example.quan_ly_sinh_vien_v2.Security.JwtAuthFilter;
import com.example.quan_ly_sinh_vien_v2.Security.JwtAuthenticationEntryPoint;
import com.example.quan_ly_sinh_vien_v2.Service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
@AutoConfigureMockMvc(addFilters = false)
class StudentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentService studentService;
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
    void myPaymentHistoryShouldReturnStudentPayments() throws Exception {
        StudentPaymentHistoryResponse.TuitionInfo tuitionInfo = new StudentPaymentHistoryResponse.TuitionInfo();
        tuitionInfo.setSemesterId(3L);
        tuitionInfo.setSemesterName("HK1");
        tuitionInfo.setFinalAmount(1_600_000D);
        tuitionInfo.setDueDate(LocalDate.of(2026, 4, 30));
        tuitionInfo.setTuitionStatus(TuitionStatus.PENDING);

        StudentPaymentHistoryResponse response = new StudentPaymentHistoryResponse();
        response.setPaymentId(10L);
        response.setTuitionId(15L);
        response.setTransactionCode("MOMO-001");
        response.setAmount(1_600_000D);
        response.setMethod(PaymentMethod.MOMO);
        response.setPaymentStatus(PaymentStatus.PENDING);
        response.setPaidAt(LocalDateTime.of(2026, 3, 26, 10, 0));
        response.setCreatedAt(LocalDateTime.of(2026, 3, 26, 9, 55));
        response.setTuition(tuitionInfo);

        when(studentService.getMyPaymentHistory(eq("student@example.com"), eq(PaymentStatus.PENDING)))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/v2/student/payments")
                        .param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Get my payment history success!"))
                .andExpect(jsonPath("$.result[0].paymentId").value(10))
                .andExpect(jsonPath("$.result[0].method").value("MOMO"))
                .andExpect(jsonPath("$.result[0].tuition.semesterName").value("HK1"));
    }
}
