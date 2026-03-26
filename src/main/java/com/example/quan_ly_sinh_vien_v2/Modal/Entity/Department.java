package com.example.quan_ly_sinh_vien_v2.Modal.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "departments")
@Getter
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @Setter
    private String code;

    @Column(nullable = false)
    @Setter
    private String name;

    @Setter
    private String description;

    @ManyToOne
    @JoinColumn(name = "head_teacher_id")
    @Setter
    private TeacherProfile headTeacher;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public Department() {
    }
}
