package com.example.quan_ly_sinh_vien_v2.Service.Admin;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.Admin.UpdateStatusEnrollment;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Enrollment;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.EnrollmentStatus;
import com.example.quan_ly_sinh_vien_v2.Repository.EnrollmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminEnrollmentServiceTest {
    @Mock
    private EnrollmentRepository enrollmentRepository;

    private AdminEnrollmentService adminEnrollmentService;

    @BeforeEach
    void setUp() {
        adminEnrollmentService = new AdminEnrollmentService(enrollmentRepository);
    }

    @Test
    void updateStatusShouldMutateEnrollmentStatus() {
        Enrollment enrollment = new Enrollment();
        enrollment.setStatus(EnrollmentStatus.ENROLLED);

        UpdateStatusEnrollment request = new UpdateStatusEnrollment();
        ReflectionTestUtils.setField(request, "status", EnrollmentStatus.COMPLETED);

        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

        adminEnrollmentService.updateStatus(1L, request);

        assertEquals(EnrollmentStatus.COMPLETED, enrollment.getStatus());
    }
}
