package com.example.quan_ly_sinh_vien_v2.Repository;

import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Payment;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByTransactionCode(String transactionCode);

    List<Payment> findAllByOrderByCreatedAtDesc();

    List<Payment> findByStatusOrderByCreatedAtDesc(PaymentStatus status);

    @Query("""
        SELECT p FROM Payment p
        WHERE (:status IS NULL OR p.status = :status)
        AND (:semesterId IS NULL OR p.tuitionFee.semester.id = :semesterId)
        AND (:studentId IS NULL OR p.student.id = :studentId)
        AND (:fromDateTime IS NULL OR p.createdAt >= :fromDateTime)
        AND (:endDateTime IS NULL OR p.createdAt <= :endDateTime)
        ORDER BY p.createdAt DESC
    """)
    List<Payment> searchPayments(
            @Param("status") PaymentStatus status,
            @Param("semesterId") Long semesterId,
            @Param("studentId") Long studentId,
            @Param("fromDateTime") LocalDateTime fromDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

    List<Payment> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    List<Payment> findByStudentIdAndStatusOrderByCreatedAtDesc(Long studentId, PaymentStatus status);

    List<Payment> findByStudent(com.example.quan_ly_sinh_vien_v2.Modal.Entity.StudentProfile student);
}
