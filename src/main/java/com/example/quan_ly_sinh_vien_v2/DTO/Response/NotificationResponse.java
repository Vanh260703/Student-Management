package com.example.quan_ly_sinh_vien_v2.DTO.Response;

import com.example.quan_ly_sinh_vien_v2.Modal.Enum.NotificationType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class NotificationResponse {
    private Long id;
    private String title;
    private String content;
    private NotificationType type;
    private Boolean isRead;
    private Long referenceId;
    private String referenceType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}

