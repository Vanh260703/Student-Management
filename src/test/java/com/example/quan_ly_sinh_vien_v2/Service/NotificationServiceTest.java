package com.example.quan_ly_sinh_vien_v2.Service;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.Admin.BroadcastNotificationRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Request.Admin.SendNotificationRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.NotificationResponse;
import com.example.quan_ly_sinh_vien_v2.Exception.NotFoundException;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Notification;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.User;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.NotificationType;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.Role;
import com.example.quan_ly_sinh_vien_v2.Repository.NotificationRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {
    @Mock private NotificationRepository notificationRepository;
    @Mock private UserRepository userRepository;

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(notificationRepository, userRepository);
    }

    private User buildUser() {
        User user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        user.setEmail("student@example.com");
        user.setPassword("password");
        user.setRole(Role.ROLE_STUDENT);
        return user;
    }

    private Notification buildNotification(Long id, User user, String title, String content, 
                                          NotificationType type, Boolean isRead) {
        Notification notification = new Notification();
        ReflectionTestUtils.setField(notification, "id", id);
        notification.setUser(user);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setIsRead(isRead);
        notification.setReferenceId(1L);
        notification.setReferenceType("GRADE");
        return notification;
    }

    @Test
    void getUserNotificationsShouldReturnAllNotificationsOrderByNewest() {
        User user = buildUser();
        Notification notification1 = buildNotification(1L, user, "Grade Posted", "Your grade for Math is posted", 
                NotificationType.GRADE, false);
        Notification notification2 = buildNotification(2L, user, "Payment Due", "Your tuition payment is due", 
                NotificationType.PAYMENT, false);

        when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(user));
        when(notificationRepository.findByUserOrderByCreatedAtDesc(eq(user)))
                .thenReturn(Arrays.asList(notification2, notification1));

        List<NotificationResponse> result = notificationService.getUserNotifications("student@example.com");

        assertEquals(2, result.size());
        assertEquals("Payment Due", result.get(0).getTitle());
        assertEquals("Grade Posted", result.get(1).getTitle());
    }

    @Test
    void getUserNotificationsShouldThrowNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> 
            notificationService.getUserNotifications("unknown@example.com"));
    }

    @Test
    void getUnreadNotificationsShouldReturnOnlyUnreadNotifications() {
        User user = buildUser();
        Notification notification1 = buildNotification(1L, user, "Unread 1", "Content 1", NotificationType.GRADE, false);
        Notification notification2 = buildNotification(2L, user, "Unread 2", "Content 2", NotificationType.PAYMENT, false);

        when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(user));
        when(notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(eq(user)))
                .thenReturn(Arrays.asList(notification2, notification1));

        List<NotificationResponse> result = notificationService.getUnreadNotifications("student@example.com");

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(n -> !n.getIsRead()));
    }

    @Test
    void getUnreadNotificationCountShouldReturnCorrectCount() {
        User user = buildUser();

        when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(user));
        when(notificationRepository.countByUserAndIsReadFalse(eq(user))).thenReturn(3L);

        Long result = notificationService.getUnreadNotificationCount("student@example.com");

        assertEquals(3L, result);
    }

    @Test
    void getNotificationByIdShouldReturnNotificationDetails() {
        User user = buildUser();
        Notification notification = buildNotification(1L, user, "Test Notification", "Test Content", 
                NotificationType.GRADE, false);

        when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(user));
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        NotificationResponse result = notificationService.getNotificationById("student@example.com", 1L);

        assertNotNull(result);
        assertEquals("Test Notification", result.getTitle());
        assertEquals("Test Content", result.getContent());
        assertEquals(NotificationType.GRADE, result.getType());
    }

    @Test
    void getNotificationByIdShouldThrowAuthorizationDeniedExceptionWhenAccessingOtherUserNotification() {
        User user1 = buildUser();
        ReflectionTestUtils.setField(user1, "id", 1L);

        User user2 = buildUser();
        ReflectionTestUtils.setField(user2, "id", 2L);
        user2.setEmail("other@example.com");

        Notification notification = buildNotification(1L, user2, "Test", "Test", NotificationType.GRADE, false);

        when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(user1));
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        assertThrows(AuthorizationDeniedException.class, () -> 
            notificationService.getNotificationById("student@example.com", 1L));
    }

    @Test
    void getNotificationByIdShouldThrowNotFoundExceptionWhenNotFound() {
        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> 
            notificationService.getNotificationById("student@example.com", 999L));
    }

    @Test
    void markAsReadShouldUpdateNotificationAndReturnResponse() {
        User user = buildUser();
        Notification notification = buildNotification(1L, user, "Test Notification", "Test Content", 
                NotificationType.GRADE, false);

        when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(user));
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        NotificationResponse result = notificationService.markAsRead("student@example.com", 1L);

        verify(notificationRepository).save(any(Notification.class));
        assertEquals("Test Notification", result.getTitle());
        assertTrue(result.getIsRead());
    }

    @Test
    void markAsReadShouldThrowAuthorizationDeniedExceptionWhenAccessingOtherUserNotification() {
        User user1 = buildUser();
        ReflectionTestUtils.setField(user1, "id", 1L);

        User user2 = buildUser();
        ReflectionTestUtils.setField(user2, "id", 2L);
        user2.setEmail("other@example.com");

        Notification notification = buildNotification(1L, user2, "Test", "Test", NotificationType.GRADE, false);

        when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(user1));
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        assertThrows(AuthorizationDeniedException.class, () -> 
            notificationService.markAsRead("student@example.com", 1L));
    }

    @Test
    void markAsReadShouldThrowNotFoundExceptionWhenNotificationNotFound() {
        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> 
            notificationService.markAsRead("student@example.com", 999L));
    }

    @Test
    void markAllAsReadShouldUpdateAllUnreadNotifications() {
        User user = buildUser();
        Notification notification1 = buildNotification(1L, user, "Unread 1", "Content 1", NotificationType.GRADE, false);
        Notification notification2 = buildNotification(2L, user, "Unread 2", "Content 2", NotificationType.PAYMENT, false);

        when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(user));
        when(notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(eq(user)))
                .thenReturn(Arrays.asList(notification1, notification2));

        notificationService.markAllAsRead("student@example.com");

        verify(notificationRepository).saveAll(any(List.class));
        assertTrue(notification1.getIsRead());
        assertTrue(notification2.getIsRead());
    }

    @Test
    void markAllAsReadShouldThrowNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> 
            notificationService.markAllAsRead("unknown@example.com"));
    }

    @Test
    void deleteNotificationShouldCallRepositoryDelete() {
        when(notificationRepository.existsById(1L)).thenReturn(true);

        notificationService.deleteNotification(1L);

        verify(notificationRepository).deleteById(1L);
    }

    @Test
    void deleteNotificationShouldThrowNotFoundExceptionWhenNotFound() {
        when(notificationRepository.existsById(999L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> notificationService.deleteNotification(999L));
    }

    @Test
    void broadcastNotificationShouldSendToAllUsersWithSpecifiedRoles() {
        User student1 = buildUser();
        ReflectionTestUtils.setField(student1, "id", 1L);
        student1.setRole(Role.ROLE_STUDENT);

        User student2 = buildUser();
        ReflectionTestUtils.setField(student2, "id", 2L);
        student2.setRole(Role.ROLE_STUDENT);

        User teacher = buildUser();
        ReflectionTestUtils.setField(teacher, "id", 3L);
        teacher.setRole(Role.ROLE_TEACHER);

        BroadcastNotificationRequest request = new BroadcastNotificationRequest();
        request.setTitle("System Update");
        request.setContent("New features available");
        request.setType(NotificationType.SYSTEM);
        request.setTargetRoles(Arrays.asList(Role.ROLE_STUDENT, Role.ROLE_TEACHER));

        when(userRepository.findByRoleIn(Arrays.asList(Role.ROLE_STUDENT, Role.ROLE_TEACHER)))
                .thenReturn(Arrays.asList(student1, student2, teacher));
        when(notificationRepository.saveAll(any(List.class))).thenReturn(Arrays.asList(new Notification(), new Notification(), new Notification()));

        int result = notificationService.broadcastNotification(request);

        assertEquals(3, result);
        verify(notificationRepository).saveAll(any(List.class));
    }

    @Test
    void broadcastNotificationShouldReturnZeroWhenNoUsersFound() {
        BroadcastNotificationRequest request = new BroadcastNotificationRequest();
        request.setTitle("Test");
        request.setContent("Test");
        request.setType(NotificationType.SYSTEM);
        request.setTargetRoles(List.of(Role.ROLE_STUDENT));

        when(userRepository.findByRoleIn(List.of(Role.ROLE_STUDENT))).thenReturn(List.of());

        int result = notificationService.broadcastNotification(request);

        assertEquals(0, result);
    }

    @Test
    void sendNotificationToUsersShouldSendToSpecifiedUsers() {
        User user1 = buildUser();
        ReflectionTestUtils.setField(user1, "id", 1L);

        User user2 = buildUser();
        ReflectionTestUtils.setField(user2, "id", 2L);

        SendNotificationRequest request = new SendNotificationRequest();
        request.setTitle("Personal Message");
        request.setContent("Hello specific users");
        request.setType(NotificationType.SYSTEM);
        request.setTargetUserIds(Arrays.asList(1L, 2L));

        when(userRepository.findAllById(Arrays.asList(1L, 2L))).thenReturn(Arrays.asList(user1, user2));
        when(notificationRepository.saveAll(any(List.class))).thenReturn(Arrays.asList(new Notification(), new Notification()));

        int result = notificationService.sendNotificationToUsers(request);

        assertEquals(2, result);
        verify(notificationRepository).saveAll(any(List.class));
    }

    @Test
    void sendNotificationToUsersShouldReturnZeroWhenNoUsersFound() {
        SendNotificationRequest request = new SendNotificationRequest();
        request.setTitle("Test");
        request.setContent("Test");
        request.setType(NotificationType.SYSTEM);
        request.setTargetUserIds(Arrays.asList(999L, 1000L));

        when(userRepository.findAllById(Arrays.asList(999L, 1000L))).thenReturn(List.of());

        int result = notificationService.sendNotificationToUsers(request);

        assertEquals(0, result);
    }
}
