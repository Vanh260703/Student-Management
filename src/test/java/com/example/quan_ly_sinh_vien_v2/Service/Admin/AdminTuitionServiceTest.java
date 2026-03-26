package com.example.quan_ly_sinh_vien_v2.Service.Admin;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.Admin.UpdateTuitionRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.Admin.GenerateTuitionResponse;
import com.example.quan_ly_sinh_vien_v2.Exception.UpdateFailException;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.ClassEntity;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Enrollment;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Semester;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.StudentProfile;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Subject;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.TuitionFee;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.User;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.EnrollmentStatus;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.SemesterName;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.TuitionStatus;
import com.example.quan_ly_sinh_vien_v2.Repository.EnrollmentRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.SemesterRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.StudentProfileRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.TuitionFeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminTuitionServiceTest {
    @Mock
    private SemesterRepository semesterRepository;
    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private TuitionFeeRepository tuitionFeeRepository;
    @Mock
    private StudentProfileRepository studentProfileRepository;

    private AdminTuitionService adminTuitionService;

    @BeforeEach
    void setUp() {
        adminTuitionService = new AdminTuitionService(
                semesterRepository,
                enrollmentRepository,
                tuitionFeeRepository,
                studentProfileRepository
        );
    }

    @Test
    void generateCurrentSemesterTuitionShouldCreatePendingTuitionAndSkipExistingStudent() {
        Semester semester = new Semester();
        semester.setName(SemesterName.HK1);
        semester.setSemesterNumber(1);
        semester.setStartDate(LocalDate.now().minusDays(10));
        semester.setEndDate(LocalDate.now().plusDays(30));
        semester.setRegistrationEnd(Instant.now().minusSeconds(60));
        org.springframework.test.util.ReflectionTestUtils.setField(semester, "id", 5L);

        Enrollment enrollmentOne = buildEnrollment(1L, 3);
        Enrollment enrollmentTwo = buildEnrollment(2L, 4);

        when(semesterRepository.findFirstByIsActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(any(), any()))
                .thenReturn(Optional.of(semester));
        when(enrollmentRepository.findBySemesterIdAndStatus(5L, EnrollmentStatus.ENROLLED))
                .thenReturn(List.of(enrollmentOne, enrollmentTwo));
        when(tuitionFeeRepository.existsByStudentIdAndSemesterId(1L, 5L)).thenReturn(false);
        when(tuitionFeeRepository.existsByStudentIdAndSemesterId(2L, 5L)).thenReturn(true);

        GenerateTuitionResponse result = adminTuitionService.generateCurrentSemesterTuition();

        ArgumentCaptor<TuitionFee> captor = ArgumentCaptor.forClass(TuitionFee.class);
        verify(tuitionFeeRepository).save(captor.capture());
        TuitionFee saved = captor.getValue();

        assertEquals(1_200_000D, saved.getFinalAmount());
        assertEquals(TuitionStatus.PENDING, saved.getStatus());
        assertEquals(1, result.getGeneratedCount());
        assertEquals(1, result.getSkippedCount());
    }

    @Test
    void updateTuitionShouldRejectAmountChangeWhenTuitionAlreadyPaid() {
        TuitionFee tuitionFee = new TuitionFee();
        tuitionFee.setStatus(TuitionStatus.PAID);
        tuitionFee.setAmount(1_000_000D);
        tuitionFee.setDiscount(0D);
        when(tuitionFeeRepository.findById(99L)).thenReturn(Optional.of(tuitionFee));

        UpdateTuitionRequest request = new UpdateTuitionRequest();
        org.springframework.test.util.ReflectionTestUtils.setField(request, "amount", 2_000_000D);

        assertThrows(UpdateFailException.class, () -> adminTuitionService.updateTuition(99L, request));
    }

    private Enrollment buildEnrollment(Long userId, int credits) {
        User user = new User();
        org.springframework.test.util.ReflectionTestUtils.setField(user, "id", userId);

        StudentProfile student = new StudentProfile();
        student.setUser(user);

        Subject subject = new Subject();
        subject.setCredits(credits);

        ClassEntity classEntity = new ClassEntity();
        classEntity.setSubject(subject);

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setClassEntity(classEntity);
        enrollment.setStatus(EnrollmentStatus.ENROLLED);

        return enrollment;
    }
}
