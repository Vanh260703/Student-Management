package com.example.quan_ly_sinh_vien_v2.Modal.Entity;

import com.example.quan_ly_sinh_vien_v2.Modal.Enum.GradeComponentType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "grade_components")
@Getter
public class GradeComponent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @Setter
    private ClassEntity classEntity;

    @Setter
    private String name;

    @Setter
    private Integer weight;

    @Enumerated(EnumType.STRING)
    @Setter
    private GradeComponentType type;

    @Setter
    private Double maxScore;

    public GradeComponent() {
    }
}
