package com.example.quan_ly_sinh_vien_v2.DTO.Response.Admin;

import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Payment;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.StudentProfile;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.PaymentMethod;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.PaymentStatus;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.TuitionStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class AdminPaymentResponse {
    private Long paymentId;
    private String transactionCode;
    private Double amount;
    private PaymentMethod method;
    private PaymentStatus status;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
    private StudentInfo student;
    private TuitionInfo tuition;

    @Getter
    @Setter
    public static class StudentInfo {
        private Long id;
        private String studentCode;
        private String fullName;
        private String email;
    }

    @Getter
    @Setter
    public static class TuitionInfo {
        private Long id;
        private Long semesterId;
        private String semesterName;
        private Double amount;
        private Double discount;
        private Double finalAmount;
        private LocalDate dueDate;
        private TuitionStatus status;
    }

    public static AdminPaymentResponse from(Payment payment, StudentProfile studentProfile) {
        AdminPaymentResponse response = new AdminPaymentResponse();
        response.setPaymentId(payment.getId());
        response.setTransactionCode(payment.getTransactionCode());
        response.setAmount(payment.getAmount());
        response.setMethod(payment.getMethod());
        response.setStatus(payment.getStatus());
        response.setPaidAt(payment.getPaidAt());
        response.setCreatedAt(payment.getCreatedAt());

        StudentInfo studentInfo = new StudentInfo();
        studentInfo.setId(payment.getStudent().getId());
        studentInfo.setFullName(payment.getStudent().getFullName());
        studentInfo.setEmail(payment.getStudent().getEmail());
        studentInfo.setStudentCode(studentProfile != null ? studentProfile.getStudentCode() : null);
        response.setStudent(studentInfo);

        TuitionInfo tuitionInfo = new TuitionInfo();
        tuitionInfo.setId(payment.getTuitionFee().getId());
        tuitionInfo.setSemesterId(payment.getTuitionFee().getSemester().getId());
        tuitionInfo.setSemesterName(payment.getTuitionFee().getSemester().getName().name());
        tuitionInfo.setAmount(payment.getTuitionFee().getAmount());
        tuitionInfo.setDiscount(payment.getTuitionFee().getDiscount());
        tuitionInfo.setFinalAmount(payment.getTuitionFee().getFinalAmount());
        tuitionInfo.setDueDate(payment.getTuitionFee().getDueDate());
        tuitionInfo.setStatus(payment.getTuitionFee().getStatus());
        response.setTuition(tuitionInfo);

        return response;
    }
}
