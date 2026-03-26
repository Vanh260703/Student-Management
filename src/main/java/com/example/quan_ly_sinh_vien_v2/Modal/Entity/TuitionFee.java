package com.example.quan_ly_sinh_vien_v2.Modal.Entity;

import com.example.quan_ly_sinh_vien_v2.Modal.Enum.TuitionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tuition_fees")
@Getter
public class TuitionFee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @Setter
    private User student;

    @ManyToOne
    @Setter
    private Semester semester;

    @Setter
    private Double amount;

    @Setter
    private Double discount;

    @Setter
    private Double finalAmount;

    @Setter
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Setter
    private TuitionStatus status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public TuitionFee() {
    }
}
