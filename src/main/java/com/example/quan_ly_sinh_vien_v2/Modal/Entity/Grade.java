package com.example.quan_ly_sinh_vien_v2.Modal.Entity;

import com.example.quan_ly_sinh_vien_v2.Modal.Enum.LetterGrade;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "grades")
@Getter
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @Setter
    private Enrollment enrollment;

    @ManyToOne
    @Setter
    private GradeComponent gradeComponent;

    @Setter
    private Double score;

    @Enumerated(EnumType.STRING)
    @Setter
    private LetterGrade letterGrade;

    @Setter
    private Boolean isPublished = false;

    @ManyToOne
    @Setter
    private TeacherProfile gradedBy;

    @Setter
    private LocalDateTime gradedAt;

    @Setter
    private String textnote;

    public Grade() {
    }
}
