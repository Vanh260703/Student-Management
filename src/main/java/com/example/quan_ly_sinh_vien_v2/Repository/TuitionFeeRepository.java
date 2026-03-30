package com.example.quan_ly_sinh_vien_v2.Repository;

import com.example.quan_ly_sinh_vien_v2.Modal.Entity.TuitionFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TuitionFeeRepository extends JpaRepository<TuitionFee, Long> {
    Optional<TuitionFee> findByIdAndStudentId(Long id, Long studentId);

    boolean existsByStudentIdAndSemesterId(Long studentId, Long semesterId);

    List<TuitionFee> findAllByOrderByCreatedAtDesc();

    List<TuitionFee> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    List<TuitionFee> findByStudentIdAndSemesterIdOrderByCreatedAtDesc(Long studentId, Long semesterId);

}
