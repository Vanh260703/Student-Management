package com.example.quan_ly_sinh_vien_v2.Service;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.Admin.BroadcastNotificationRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Request.Admin.SendNotificationRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.NotificationResponse;
import com.example.quan_ly_sinh_vien_v2.Exception.NotFoundException;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Notification;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.User;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.Role;
import com.example.quan_ly_sinh_vien_v2.Repository.NotificationRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.UserRepository;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    /**
     * Lấy tất cả thông báo của người dùng
     */
    public List<NotificationResponse> getUserNotifications(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));

        List<Notification> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user);
        return notifications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lấy các thông báo chưa đọc
     */
    public List<NotificationResponse> getUnreadNotifications(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));

        List<Notification> notifications = notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user);
        return notifications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Đếm số lượng thông báo chưa đọc
     */
    public Long getUnreadNotificationCount(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));

        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    /**
     * Lấy chi tiết một thông báo
     */
    public NotificationResponse getNotificationById(String username, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("Notification not found with id: " + notificationId));

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + username));

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new AuthorizationDeniedException("You are not authorized to view this notification");
        }

        return convertToResponse(notification);
    }

    /**
     * Đánh dấu thông báo là đã đọc
     */
    @Transactional
    public NotificationResponse markAsRead(String username, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("Notification not found with id: " + notificationId));

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + username));

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new AuthorizationDeniedException("You are not authorized to mark this notification as read");
        }

        notification.setIsRead(true);
        Notification updated = notificationRepository.save(notification);
        return convertToResponse(updated);
    }

    /**
     * Đánh dấu tất cả thông báo là đã đọc
     */
    @Transactional
    public void markAllAsRead(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));

        List<Notification> unreadNotifications = notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user);
        unreadNotifications.forEach(notification -> notification.setIsRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }

    /**
     * Xóa thông báo
     */
    @Transactional
    public void deleteNotification(Long notificationId) {
        if (!notificationRepository.existsById(notificationId)) {
            throw new NotFoundException("Notification not found with id: " + notificationId);
        }
        notificationRepository.deleteById(notificationId);
    }

    /**
     * Gửi thông báo hàng loạt theo role
     */
    @Transactional
    public int broadcastNotification(BroadcastNotificationRequest request) {
        List<User> targetUsers = userRepository.findByRoleIn(request.getTargetRoles());

        if (targetUsers.isEmpty()) {
            return 0;
        }

        List<Notification> notifications = targetUsers.stream()
                .map(user -> {
                    Notification notification = new Notification();
                    notification.setUser(user);
                    notification.setTitle(request.getTitle());
                    notification.setContent(request.getContent());
                    notification.setType(request.getType());
                    notification.setReferenceId(request.getReferenceId());
                    notification.setReferenceType(request.getReferenceType());
                    notification.setIsRead(false);
                    return notification;
                })
                .collect(Collectors.toList());

        List<Notification> savedNotifications = notificationRepository.saveAll(notifications);
        return savedNotifications.size();
    }

    /**
     * Gửi thông báo cho user cụ thể
     */
    @Transactional
    public int sendNotificationToUsers(SendNotificationRequest request) {
        List<User> targetUsers = userRepository.findAllById(request.getTargetUserIds());

        if (targetUsers.isEmpty()) {
            return 0;
        }

        List<Notification> notifications = targetUsers.stream()
                .map(user -> {
                    Notification notification = new Notification();
                    notification.setUser(user);
                    notification.setTitle(request.getTitle());
                    notification.setContent(request.getContent());
                    notification.setType(request.getType());
                    notification.setReferenceId(request.getReferenceId());
                    notification.setReferenceType(request.getReferenceType());
                    notification.setIsRead(false);
                    return notification;
                })
                .collect(Collectors.toList());

        List<Notification> savedNotifications = notificationRepository.saveAll(notifications);
        return savedNotifications.size();
    }

    /**
     * Convert Notification entity to NotificationResponse DTO
     */
    private NotificationResponse convertToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .type(notification.getType())
                .isRead(notification.getIsRead())
                .referenceId(notification.getReferenceId())
                .referenceType(notification.getReferenceType())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}

