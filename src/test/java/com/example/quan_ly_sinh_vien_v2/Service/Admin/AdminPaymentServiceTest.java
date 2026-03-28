package com.example.quan_ly_sinh_vien_v2.Service.Admin;

import com.example.quan_ly_sinh_vien_v2.DTO.Response.Admin.AdminPaymentResponse;
import com.example.quan_ly_sinh_vien_v2.Exception.UpdateFailException;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Payment;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Semester;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.StudentProfile;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.TuitionFee;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.User;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.PaymentMethod;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.PaymentStatus;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.SemesterName;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.TuitionStatus;
import com.example.quan_ly_sinh_vien_v2.Repository.PaymentRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.StudentProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminPaymentServiceTest {
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private StudentProfileRepository studentProfileRepository;

    private AdminPaymentService adminPaymentService;

    @BeforeEach
    void setUp() {
        adminPaymentService = new AdminPaymentService(paymentRepository, studentProfileRepository);
    }

    @Test
    void getAllPaymentsShouldReturnAllPaymentsWhenStatusIsNull() {
        Payment payment = buildPayment(1L, PaymentStatus.SUCCESS);
        StudentProfile studentProfile = new StudentProfile();
        studentProfile.setStudentCode("SV001");

        when(paymentRepository.searchPayments(null, null, null, null, null)).thenReturn(List.of(payment));
        when(studentProfileRepository.findByUserId(1L)).thenReturn(Optional.of(studentProfile));

        List<AdminPaymentResponse> result = adminPaymentService.getAllPayments(null, null, null, null, null);

        assertEquals(1, result.size());
        assertEquals("SV001", result.get(0).getStudent().getStudentCode());
        assertEquals(PaymentStatus.SUCCESS, result.get(0).getStatus());
        verify(paymentRepository).searchPayments(null, null, null, null, null);
    }

    @Test
    void getAllPaymentsShouldFilterByStatusWhenProvided() {
        Payment payment = buildPayment(2L, PaymentStatus.FAILED);

        when(paymentRepository.searchPayments(
                PaymentStatus.FAILED,
                10L,
                2L,
                LocalDate.of(2026, 3, 1).atStartOfDay(),
                LocalDate.of(2026, 3, 31).atTime(23, 59, 59)
        ))
                .thenReturn(List.of(payment));
        when(studentProfileRepository.findByUserId(2L)).thenReturn(Optional.empty());

        List<AdminPaymentResponse> result = adminPaymentService.getAllPayments(
                PaymentStatus.FAILED,
                10L,
                2L,
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2026, 3, 31)
        );

        assertEquals(1, result.size());
        assertEquals(PaymentStatus.FAILED, result.get(0).getStatus());
        assertEquals(null, result.get(0).getStudent().getStudentCode());
        verify(paymentRepository).searchPayments(
                PaymentStatus.FAILED,
                10L,
                2L,
                LocalDate.of(2026, 3, 1).atStartOfDay(),
                LocalDate.of(2026, 3, 31).atTime(23, 59, 59)
        );
    }

    @Test
    void getAllPaymentsShouldRejectInvalidDateRange() {
        assertThrows(
                UpdateFailException.class,
                () -> adminPaymentService.getAllPayments(
                        null,
                        null,
                        null,
                        LocalDate.of(2026, 4, 1),
                        LocalDate.of(2026, 3, 1)
                )
        );
    }

    private Payment buildPayment(Long userId, PaymentStatus paymentStatus) {
        User user = new User();
        user.setFullName("Test Student");
        user.setEmail("student@example.com");
        org.springframework.test.util.ReflectionTestUtils.setField(user, "id", userId);

        Semester semester = new Semester();
        semester.setName(SemesterName.HK1);
        org.springframework.test.util.ReflectionTestUtils.setField(semester, "id", 10L);

        TuitionFee tuitionFee = new TuitionFee();
        tuitionFee.setStudent(user);
        tuitionFee.setSemester(semester);
        tuitionFee.setAmount(1_000_000D);
        tuitionFee.setDiscount(100_000D);
        tuitionFee.setFinalAmount(900_000D);
        tuitionFee.setDueDate(LocalDate.of(2026, 3, 31));
        tuitionFee.setStatus(TuitionStatus.PAID);
        org.springframework.test.util.ReflectionTestUtils.setField(tuitionFee, "id", 20L);

        Payment payment = new Payment();
        payment.setStudent(user);
        payment.setTuitionFee(tuitionFee);
        payment.setAmount(900_000D);
        payment.setMethod(PaymentMethod.MOMO);
        payment.setTransactionCode("TXN-" + userId);
        payment.setStatus(paymentStatus);
        payment.setPaidAt(LocalDateTime.of(2026, 3, 27, 10, 30));
        org.springframework.test.util.ReflectionTestUtils.setField(payment, "id", 30L + userId);
        org.springframework.test.util.ReflectionTestUtils.setField(payment, "createdAt", LocalDateTime.of(2026, 3, 27, 10, 0));

        return payment;
    }
}
