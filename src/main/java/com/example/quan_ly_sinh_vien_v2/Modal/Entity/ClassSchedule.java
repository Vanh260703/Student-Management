package com.example.quan_ly_sinh_vien_v2.Modal.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "class_schedules")
@Getter
public class ClassSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @Setter
    private ClassEntity classEntity;

    @Setter
    private Integer dayOfWeek;

    @Setter
    private Integer startPeriod;

    @Setter
    private Integer endPeriod;

    @Setter
    private String room;

    @Setter
    private LocalDate weekStart;

    @Setter
    private LocalDate weekEnd;

    public ClassSchedule() {
    }
}
