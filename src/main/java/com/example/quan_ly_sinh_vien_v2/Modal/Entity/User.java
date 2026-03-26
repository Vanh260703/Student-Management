package com.example.quan_ly_sinh_vien_v2.Modal.Entity;

import com.example.quan_ly_sinh_vien_v2.Modal.Enum.Gender;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Column(unique = true, nullable = false)
    @Getter
    @Setter
    private String email;

    @Column(nullable = false)
    @Getter
    @Setter
    private String password;

    @Column(nullable = false)
    @Getter
    @Setter
    private String fullName;

    @Column(nullable = false)
    @Getter
    @Setter
    private String personalEmail;

    @Getter
    @Setter
    private String normalizeName;

    @Getter
    @Setter
    private String phone;

    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private Gender gender;

    @Getter
    @Setter
    private String avatarUrl = "https://www.svgrepo.com/show/452030/avatar-default.svg";

    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private Role role;

    @Getter
    @Setter
    private String address;

    @Getter
    @Setter
    private LocalDate dateOfBirth;

    @Getter
    @Setter
    private Boolean isActive = true;

    @Getter
    @Setter
    private Boolean isDelete = false;

    @Getter
    @Setter
    private Boolean isSendMail = false;

    @CreationTimestamp
    @Column(updatable = false)
    @Getter
    private LocalDateTime createdAt;

    @Getter
    @Setter
    private LocalDateTime updatedAt;

    public User() {
    }
}
