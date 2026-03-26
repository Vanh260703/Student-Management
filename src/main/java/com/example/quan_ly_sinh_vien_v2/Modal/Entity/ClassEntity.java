package com.example.quan_ly_sinh_vien_v2.Modal.Entity;

import com.example.quan_ly_sinh_vien_v2.Modal.Enum.ClassStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "classes")
@Getter
public class ClassEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @Setter
    private Semester semester;

    @ManyToOne
    @Setter
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    @Setter
    private TeacherProfile teacher;

    @Column(unique = true)
    @Setter
    private String classCode;

    @Setter
    private Integer maxStudents;

    @Setter
    private Integer currentStudents;

    @Setter
    private String room;

    @Setter
    private String scheduleInfo;

    @Enumerated(EnumType.STRING)
    @Setter
    private ClassStatus status = ClassStatus.CLOSE;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public ClassEntity() {
    }
}
