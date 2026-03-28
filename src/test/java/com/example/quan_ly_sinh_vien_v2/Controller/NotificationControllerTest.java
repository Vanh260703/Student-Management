package com.example.quan_ly_sinh_vien_v2.Controller;

import com.example.quan_ly_sinh_vien_v2.DTO.Response.NotificationResponse;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.NotificationType;
import com.example.quan_ly_sinh_vien_v2.Security.JwtAccessDeniedHandler;
import com.example.quan_ly_sinh_vien_v2.Security.JwtAuthFilter;
import com.example.quan_ly_sinh_vien_v2.Security.JwtAuthenticationEntryPoint;
import com.example.quan_ly_sinh_vien_v2.Service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
class NotificationControllerTest {
    @Autowired
    private MockMvc mockMvc;

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

    private NotificationResponse buildNotificationResponse(Long id, String title, String content, 
                                                           NotificationType type, Boolean isRead) {
        return NotificationResponse.builder()
                .id(id)
                .title(title)
                .content(content)
                .type(type)
                .isRead(isRead)
                .referenceId(1L)
                .referenceType("GRADE")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @WithMockUser(username = "student@example.com", roles = "STUDENT")
    void getAllNotificationsShouldReturnListOfNotifications() throws Exception {
        NotificationResponse notification1 = buildNotificationResponse(1L, "Grade Posted", "Your grade for Math is posted",
                NotificationType.GRADE, false);
        NotificationResponse notification2 = buildNotificationResponse(2L, "Payment Due", "Your tuition payment is due",
                NotificationType.PAYMENT, false);

        when(notificationService.getUserNotifications(eq("student@example.com")))
                .thenReturn(Arrays.asList(notification1, notification2));

        mockMvc.perform(get("/api/v2/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Get all notifications successfully!"))
                .andExpect(jsonPath("$.result").isArray())
                .andExpect(jsonPath("$.result.length()").value(2))
                .andExpect(jsonPath("$.result[0].title").value("Grade Posted"))
                .andExpect(jsonPath("$.result[0].type").value("GRADE"))
                .andExpect(jsonPath("$.result[1].title").value("Payment Due"))
                .andExpect(jsonPath("$.result[1].type").value("PAYMENT"));
    }

    @Test
    @WithMockUser(username = "student@example.com", roles = "STUDENT")
    void getUnreadNotificationsShouldReturnOnlyUnreadNotifications() throws Exception {
        NotificationResponse notification1 = buildNotificationResponse(1L, "Unread 1", "Content 1",
                NotificationType.GRADE, false);
        NotificationResponse notification2 = buildNotificationResponse(2L, "Unread 2", "Content 2",
                NotificationType.SCHEDULE, false);

        when(notificationService.getUnreadNotifications(eq("student@example.com")))
                .thenReturn(Arrays.asList(notification1, notification2));

        mockMvc.perform(get("/api/v2/notifications/unread"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Get unread notifications successfully!"))
                .andExpect(jsonPath("$.result.length()").value(2))
                .andExpect(jsonPath("$.result[0].isRead").value(false))
                .andExpect(jsonPath("$.result[1].isRead").value(false));
    }

    @Test
    @WithMockUser(username = "student@example.com", roles = "STUDENT")
    void getUnreadCountShouldReturnNumberOfUnreadNotifications() throws Exception {
        when(notificationService.getUnreadNotificationCount(eq("student@example.com")))
                .thenReturn(5L);

        mockMvc.perform(get("/api/v2/notifications/unread/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Get unread notification count successfully!"))
                .andExpect(jsonPath("$.result").value(5));
    }

    @Test
    @WithMockUser(username = "student@example.com", roles = "STUDENT")
    void getNotificationByIdShouldReturnNotificationDetails() throws Exception {
        NotificationResponse notification = buildNotificationResponse(1L, "Test Notification", "Test Content",
                NotificationType.GRADE, false);

        when(notificationService.getNotificationById(eq("student@example.com"), eq(1L)))
                .thenReturn(notification);

        mockMvc.perform(get("/api/v2/notifications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Get notification successfully!"))
                .andExpect(jsonPath("$.result.id").value(1))
                .andExpect(jsonPath("$.result.title").value("Test Notification"))
                .andExpect(jsonPath("$.result.content").value("Test Content"))
                .andExpect(jsonPath("$.result.type").value("GRADE"));
    }

    @Test
    @WithMockUser(username = "student@example.com", roles = "STUDENT")
    void getNotificationByIdShouldReturn403WhenAccessingOtherUserNotification() throws Exception {
        when(notificationService.getNotificationById(eq("student@example.com"), eq(999L)))
                .thenThrow(new AuthorizationDeniedException("You are not authorized to view this notification"));

        mockMvc.perform(get("/api/v2/notifications/999"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "student@example.com", roles = "STUDENT")
    void markAsReadShouldUpdateNotificationAndReturnIt() throws Exception {
        NotificationResponse notification = buildNotificationResponse(1L, "Test Notification", "Test Content",
                NotificationType.GRADE, true);

        when(notificationService.markAsRead(eq("student@example.com"), eq(1L)))
                .thenReturn(notification);

        mockMvc.perform(put("/api/v2/notifications/1/mark-as-read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Mark notification as read successfully!"))
                .andExpect(jsonPath("$.result.isRead").value(true));
    }

    @Test
    @WithMockUser(username = "student@example.com", roles = "STUDENT")
    void markAsReadShouldReturn403WhenAccessingOtherUserNotification() throws Exception {
        when(notificationService.markAsRead(eq("student@example.com"), eq(999L)))
                .thenThrow(new AuthorizationDeniedException("You are not authorized to mark this notification as read"));

        mockMvc.perform(put("/api/v2/notifications/999/mark-as-read"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "student@example.com", roles = "STUDENT")
    void markAllAsReadShouldUpdateAllUnreadNotifications() throws Exception {
        doNothing().when(notificationService).markAllAsRead(eq("student@example.com"));

        mockMvc.perform(put("/api/v2/notifications/mark-all-as-read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Mark all notifications as read successfully!"))
                .andExpect(jsonPath("$.result").doesNotExist());

        verify(notificationService).markAllAsRead("student@example.com");
    }

    @Test
    @WithMockUser(username = "student@example.com", roles = "STUDENT")
    void deleteNotificationShouldRemoveNotification() throws Exception {
        doNothing().when(notificationService).deleteNotification(eq(1L));

        mockMvc.perform(delete("/api/v2/notifications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Delete notification successfully!"))
                .andExpect(jsonPath("$.result").doesNotExist());

        verify(notificationService).deleteNotification(1L);
    }
}

