package com.example.quan_ly_sinh_vien_v2.Modal.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "teacher_profiles")
@Getter
public class TeacherProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @Setter
    private User user;

    @Setter
    private String teacherCode;

    @ManyToOne
    @Setter
    private Department department;

    @Setter
    private String degree;

    @Setter
    private String specialization;

    @Setter
    private LocalDate joinedDate;

    public TeacherProfile() {
    }
}
