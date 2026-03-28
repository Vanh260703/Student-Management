package com.example.quan_ly_sinh_vien_v2.Controller.Admin;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.Admin.BroadcastNotificationRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Request.Admin.SendNotificationRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.APIResponse;
import com.example.quan_ly_sinh_vien_v2.Service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/admin/notifications")
@PreAuthorize("hasRole('ADMIN')")
public class AdminNotificationController {
    private final NotificationService notificationService;

    public AdminNotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * [POST] /api/v2/admin/notifications/broadcast
     * Gửi thông báo hàng loạt theo role
     */
    @PostMapping("/broadcast")
    public ResponseEntity<?> broadcastNotification(@Valid @RequestBody BroadcastNotificationRequest request) {
        int sentCount = notificationService.broadcastNotification(request);

        APIResponse response = new APIResponse<>(
                200,
                "Broadcast notification sent successfully to " + sentCount + " users!",
                sentCount
        );

        return ResponseEntity.ok(response);
    }

    /**
     * [POST] /api/v2/admin/notifications/send
     * Gửi thông báo cho user cụ thể
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendNotification(@Valid @RequestBody SendNotificationRequest request) {
        int sentCount = notificationService.sendNotificationToUsers(request);

        APIResponse response = new APIResponse<>(
                200,
                "Notification sent successfully to " + sentCount + " users!",
                sentCount
        );

        return ResponseEntity.ok(response);
    }
}

