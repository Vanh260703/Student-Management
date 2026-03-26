package com.example.quan_ly_sinh_vien_v2.Modal.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "program_subjects")
@Getter
public class ProgramSubject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "program_id")
    @Setter
    private Program program;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    @Setter
    private Subject subject;

    @Setter
    private Integer semester;

    @Setter
    private Boolean isRequired = false;

    @ManyToOne
    @JoinColumn(name = "prerequisite_subject_id")
    @Setter
    private Subject prerequisiteSubject;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public ProgramSubject() {
    }
}
