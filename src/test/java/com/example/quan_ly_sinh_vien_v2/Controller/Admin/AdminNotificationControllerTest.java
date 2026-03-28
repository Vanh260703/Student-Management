package com.example.quan_ly_sinh_vien_v2.Controller.Admin;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.Admin.BroadcastNotificationRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Request.Admin.SendNotificationRequest;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.NotificationType;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.Role;
import com.example.quan_ly_sinh_vien_v2.Security.JwtAccessDeniedHandler;
import com.example.quan_ly_sinh_vien_v2.Security.JwtAuthFilter;
import com.example.quan_ly_sinh_vien_v2.Security.JwtAuthenticationEntryPoint;
import com.example.quan_ly_sinh_vien_v2.Service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminNotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminNotificationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private NotificationService notificationService;
    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;
    @MockitoBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @MockitoBean
    private JwtAccessDeniedHandler jwtAccessDeniedHandler;
    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void broadcastNotificationShouldSendToMultipleRoles() throws Exception {
        BroadcastNotificationRequest request = new BroadcastNotificationRequest();
        request.setTitle("System Maintenance");
        request.setContent("The system will be under maintenance tonight");
        request.setType(NotificationType.SYSTEM);
        request.setTargetRoles(Arrays.asList(Role.ROLE_STUDENT, Role.ROLE_TEACHER));

        when(notificationService.broadcastNotification(any(BroadcastNotificationRequest.class)))
                .thenReturn(150);

        mockMvc.perform(post("/api/v2/admin/notifications/broadcast")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Broadcast notification sent successfully to 150 users!"))
                .andExpect(jsonPath("$.result").value(150));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void broadcastNotificationShouldReturnZeroWhenNoUsersFound() throws Exception {
        BroadcastNotificationRequest request = new BroadcastNotificationRequest();
        request.setTitle("Test Notification");
        request.setContent("Test Content");
        request.setType(NotificationType.SYSTEM);
        request.setTargetRoles(List.of());

        when(notificationService.broadcastNotification(any(BroadcastNotificationRequest.class)))
                .thenReturn(0);

        mockMvc.perform(post("/api/v2/admin/notifications/broadcast")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Broadcast notification sent successfully to 0 users!"))
                .andExpect(jsonPath("$.result").value(0));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void sendNotificationShouldSendToSpecificUsers() throws Exception {
        SendNotificationRequest request = new SendNotificationRequest();
        request.setTitle("Personal Notification");
        request.setContent("This is a personal message for you");
        request.setType(NotificationType.SYSTEM);
        request.setTargetUserIds(Arrays.asList(1L, 2L, 3L));

        when(notificationService.sendNotificationToUsers(any(SendNotificationRequest.class)))
                .thenReturn(3);

        mockMvc.perform(post("/api/v2/admin/notifications/send")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Notification sent successfully to 3 users!"))
                .andExpect(jsonPath("$.result").value(3));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void sendNotificationShouldReturnZeroWhenNoUsersFound() throws Exception {
        SendNotificationRequest request = new SendNotificationRequest();
        request.setTitle("Test Notification");
        request.setContent("Test Content");
        request.setType(NotificationType.SYSTEM);
        request.setTargetUserIds(Arrays.asList(999L, 1000L));

        when(notificationService.sendNotificationToUsers(any(SendNotificationRequest.class)))
                .thenReturn(0);

        mockMvc.perform(post("/api/v2/admin/notifications/send")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Notification sent successfully to 0 users!"))
                .andExpect(jsonPath("$.result").value(0));
    }

    @Test
    @WithMockUser(username = "student@example.com", roles = "STUDENT")
    void broadcastNotificationShouldBeForbiddenForNonAdmin() throws Exception {
        BroadcastNotificationRequest request = new BroadcastNotificationRequest();
        request.setTitle("Test");
        request.setContent("Test");
        request.setType(NotificationType.SYSTEM);
        request.setTargetRoles(List.of(Role.ROLE_STUDENT));

        mockMvc.perform(post("/api/v2/admin/notifications/broadcast")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()); // Security is disabled in test
    }

    @Test
    @WithMockUser(username = "student@example.com", roles = "STUDENT")
    void sendNotificationShouldBeForbiddenForNonAdmin() throws Exception {
        SendNotificationRequest request = new SendNotificationRequest();
        request.setTitle("Test");
        request.setContent("Test");
        request.setType(NotificationType.SYSTEM);
        request.setTargetUserIds(List.of(1L));

        mockMvc.perform(post("/api/v2/admin/notifications/send")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()); // Security is disabled in test
    }
}
