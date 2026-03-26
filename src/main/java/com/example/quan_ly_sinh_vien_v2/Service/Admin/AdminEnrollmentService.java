package com.example.quan_ly_sinh_vien_v2.Service.Admin;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.Admin.UpdateStatusEnrollment;
import com.example.quan_ly_sinh_vien_v2.Exception.NotFoundException;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Enrollment;
import com.example.quan_ly_sinh_vien_v2.Repository.EnrollmentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class AdminEnrollmentService {
    private final EnrollmentRepository enrollmentRepository;

    public AdminEnrollmentService(EnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }

    // Update status enrollment
    @Transactional
    public void updateStatus(Long enrollmentId, UpdateStatusEnrollment request) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new NotFoundException("Enrollment not found!"));

        enrollment.setStatus(request.getStatus());
    }
}
