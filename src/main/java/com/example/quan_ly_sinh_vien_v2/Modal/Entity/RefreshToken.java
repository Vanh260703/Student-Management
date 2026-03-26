package com.example.quan_ly_sinh_vien_v2.Modal.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
@Getter
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @Setter
    private User user;

    @Column(unique = true, columnDefinition = "TEXT")
    @Setter
    private String token;

    @Setter
    private Instant expiresAt;

    @Setter
    private Instant revokedAt;

    public RefreshToken() {
    }

    public RefreshToken(User user, String token, Instant expiresAt) {
        this.user = user;
        this.token = token;
        this.expiresAt = expiresAt;
    }
}
