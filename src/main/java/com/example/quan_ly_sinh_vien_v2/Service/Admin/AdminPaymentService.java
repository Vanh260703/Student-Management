package com.example.quan_ly_sinh_vien_v2.Service.Admin;

import com.example.quan_ly_sinh_vien_v2.DTO.Response.Admin.AdminPaymentResponse;
import com.example.quan_ly_sinh_vien_v2.Exception.UpdateFailException;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Payment;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.StudentProfile;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.PaymentStatus;
import com.example.quan_ly_sinh_vien_v2.Repository.PaymentRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.StudentProfileRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminPaymentService {
    private final PaymentRepository paymentRepository;
    private final StudentProfileRepository studentProfileRepository;

    public AdminPaymentService(
            PaymentRepository paymentRepository,
            StudentProfileRepository studentProfileRepository
    ) {
        this.paymentRepository = paymentRepository;
        this.studentProfileRepository = studentProfileRepository;
    }

    public List<AdminPaymentResponse> getAllPayments(
            PaymentStatus status,
            Long semesterId,
            Long studentId,
            LocalDate fromDate,
            LocalDate endDate
    ) {
        if (fromDate != null && endDate != null && fromDate.isAfter(endDate)) {
            throw new UpdateFailException("fromDate must be less than or equal to endDate!");
        }

        LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(23, 59, 59) : null;

        List<Payment> payments = paymentRepository.searchPayments(
                status,
                semesterId,
                studentId,
                fromDateTime,
                endDateTime
        );

        List<AdminPaymentResponse> responses = new ArrayList<>();
        for (Payment payment : payments) {
            StudentProfile studentProfile = studentProfileRepository.findByUserId(payment.getStudent().getId())
                    .orElse(null);
            responses.add(AdminPaymentResponse.from(payment, studentProfile));
        }

        return responses;
    }
}
