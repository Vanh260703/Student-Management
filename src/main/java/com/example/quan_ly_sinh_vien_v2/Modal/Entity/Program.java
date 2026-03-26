package com.example.quan_ly_sinh_vien_v2.Modal.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "programs")
@Getter
public class Program {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "department_id")
    @Setter
    private Department department;

    @Column(unique = true, nullable = false)
    @Setter
    private String code;

    @Setter
    private String name;

    @Setter
    private String normalizeName;

    @Setter
    private Integer totalCredits;

    @Setter
    private Integer durationYears;

    @Setter
    private String description;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public Program() {
    }
}
