package com.example.quan_ly_sinh_vien_v2.Controller;

import com.example.quan_ly_sinh_vien_v2.DTO.Response.APIResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.NotificationResponse;
import com.example.quan_ly_sinh_vien_v2.Service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/notifications")
@PreAuthorize("isAuthenticated()")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * [GET] /api/v2/notifications
     * Lấy tất cả thông báo của người dùng hiện tại
     */
    @GetMapping
    public ResponseEntity<?> getAllNotifications(@AuthenticationPrincipal UserDetails userDetails) {
        List<NotificationResponse> notifications = notificationService.getUserNotifications(userDetails.getUsername());
        
        APIResponse response = new APIResponse<>(
                200,
                "Get all notifications successfully!",
                notifications
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * [GET] /api/v2/notifications/unread
     * Lấy các thông báo chưa đọc
     */
    @GetMapping("/unread")
    public ResponseEntity<?> getUnreadNotifications(@AuthenticationPrincipal UserDetails userDetails) {
        List<NotificationResponse> notifications = notificationService.getUnreadNotifications(userDetails.getUsername());
        
        APIResponse response = new APIResponse<>(
                200,
                "Get unread notifications successfully!",
                notifications
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * [GET] /api/v2/notifications/unread/count
     * Đếm số thông báo chưa đọc
     */
    @GetMapping("/unread/count")
    public ResponseEntity<?> getUnreadCount(@AuthenticationPrincipal UserDetails userDetails) {
        Long unreadCount = notificationService.getUnreadNotificationCount(userDetails.getUsername());
        
        APIResponse response = new APIResponse<>(
                200,
                "Get unread notification count successfully!",
                unreadCount
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * [GET] /api/v2/notifications/:id
     * Lấy chi tiết một thông báo
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getNotificationById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        NotificationResponse notification = notificationService.getNotificationById(userDetails.getUsername(), id);
        
        APIResponse response = new APIResponse<>(
                200,
                "Get notification successfully!",
                notification
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * [PUT] /api/v2/notifications/:id/mark-as-read
     * Đánh dấu thông báo là đã đọc
     */
    @PutMapping("/{id}/mark-as-read")
    public ResponseEntity<?> markAsRead(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        NotificationResponse notification = notificationService.markAsRead(userDetails.getUsername(), id);
        
        APIResponse response = new APIResponse<>(
                200,
                "Mark notification as read successfully!",
                notification
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * [PUT] /api/v2/notifications/mark-all-as-read
     * Đánh dấu tất cả thông báo là đã đọc
     */
    @PutMapping("/mark-all-as-read")
    public ResponseEntity<?> markAllAsRead(@AuthenticationPrincipal UserDetails userDetails) {
        notificationService.markAllAsRead(userDetails.getUsername());
        
        APIResponse response = new APIResponse<>(
                200,
                "Mark all notifications as read successfully!",
                null
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * [DELETE] /api/v2/notifications/:id
     * Xóa thông báo
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        
        APIResponse response = new APIResponse<>(
                200,
                "Delete notification successfully!",
                null
        );
        
        return ResponseEntity.ok(response);
    }
}

