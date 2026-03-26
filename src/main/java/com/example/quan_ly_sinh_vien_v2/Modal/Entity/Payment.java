package com.example.quan_ly_sinh_vien_v2.Modal.Entity;

import com.example.quan_ly_sinh_vien_v2.Modal.Enum.PaymentMethod;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @Setter
    private TuitionFee tuitionFee;

    @ManyToOne
    @Setter
    private User student;

    @Setter
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Setter
    private PaymentMethod method;

    @Column(unique = true)
    @Setter
    private String transactionCode;

    @Enumerated(EnumType.STRING)
    @Setter
    private PaymentStatus status;

    @Setter
    private LocalDateTime paidAt;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Setter
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String gatewayResponse;

    public Payment() {
    }
}
