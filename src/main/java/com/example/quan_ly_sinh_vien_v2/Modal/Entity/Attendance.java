package com.example.quan_ly_sinh_vien_v2.Modal.Entity;

import com.example.quan_ly_sinh_vien_v2.Modal.Enum.AttendanceStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendances")
@Getter
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @Setter
    private Enrollment enrollment;

    @ManyToOne
    @JoinColumn(name = "class_id")
    @Setter
    private ClassEntity classEntity;

    @Setter
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Setter
    private AttendanceStatus status;

    @ManyToOne
    @Setter
    private User notedBy;

    @Setter
    private String textnote;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public Attendance() {
    }
}
