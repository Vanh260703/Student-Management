package com.example.quan_ly_sinh_vien_v2.DTO.Request.Admin;

import com.example.quan_ly_sinh_vien_v2.Modal.Enum.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SendNotificationRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    @NotNull(message = "Type is required")
    private NotificationType type;

    private Long referenceId;
    private String referenceType;

    @NotNull(message = "Target user IDs are required")
    private List<Long> targetUserIds;
}

