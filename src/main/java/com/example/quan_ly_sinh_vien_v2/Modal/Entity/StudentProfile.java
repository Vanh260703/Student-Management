package com.example.quan_ly_sinh_vien_v2.Modal.Entity;

import com.example.quan_ly_sinh_vien_v2.Modal.Enum.Gender;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.StudentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "student_profiles")
@Getter
public class StudentProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @Setter
    private User user;

    @Setter
    private String studentCode;

    @ManyToOne
    @Setter
    private Program program;

    @ManyToOne
    @Setter
    private Department department;

    @Setter
    private Integer enrollmentYear;

    @Setter
    private String className;

    @Setter
    private Float gpa;

    @Setter
    private Integer accumulatedCredits;

    @Enumerated(EnumType.STRING)
    @Setter
    private StudentStatus status;

    public StudentProfile() {
    }
}
