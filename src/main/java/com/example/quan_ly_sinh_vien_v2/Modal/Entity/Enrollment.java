package com.example.quan_ly_sinh_vien_v2.Modal.Entity;

import com.example.quan_ly_sinh_vien_v2.Modal.Enum.EnrollmentStatus;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.LetterGrade;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments")
@Getter
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @Setter
    private StudentProfile student;

    @ManyToOne
    @Setter
    private ClassEntity classEntity;

    @Setter
    private LocalDateTime enrolledAt;

    @Enumerated(EnumType.STRING)
    @Setter
    private EnrollmentStatus status;

    @Setter
    private Double finalScore;

    @Setter
    private LetterGrade finalLetterGrade;

    @Setter
    private Boolean isPassed;

    public Enrollment() {
    }
}
