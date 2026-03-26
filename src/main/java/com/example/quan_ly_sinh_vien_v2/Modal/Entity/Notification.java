package com.example.quan_ly_sinh_vien_v2.Modal.Entity;

import com.example.quan_ly_sinh_vien_v2.Modal.Enum.NotificationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @Setter
    private User user;

    @Setter
    private String title;

    @Setter
    private String content;

    @Enumerated(EnumType.STRING)
    @Setter
    private NotificationType type;

    @Setter
    private Boolean isRead = false;

    @Setter
    private Long referenceId;

    @Setter
    private String referenceType;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public Notification() {
    }
}
