package com.example.quan_ly_sinh_vien_v2.Modal.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "subjects")
@Getter
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "department_id")
    @Setter
    private Department department;

    @Column(unique = true)
    @Setter
    private String code;

    @Column(nullable = false)
    @Setter
    private String name;

    @Setter
    private String normalizeName;

    @Setter
    private Integer credits;

    @Setter
    private String description;

    @Setter
    private Boolean isActive;

    public Subject() {
    }
}
