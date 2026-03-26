package com.example.quan_ly_sinh_vien_v2.DTO.Response;

import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Payment;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.PaymentMethod;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.PaymentStatus;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.TuitionStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class StudentPaymentHistoryResponse {
    private Long paymentId;
    private Long tuitionId;
    private String transactionCode;
    private Double amount;
    private PaymentMethod method;
    private PaymentStatus paymentStatus;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
    private TuitionInfo tuition;

    @Getter
    @Setter
    public static class TuitionInfo {
        private Long semesterId;
        private String semesterName;
        private Double finalAmount;
        private LocalDate dueDate;
        private TuitionStatus tuitionStatus;
    }

    public static StudentPaymentHistoryResponse from(Payment payment) {
        StudentPaymentHistoryResponse response = new StudentPaymentHistoryResponse();
        response.setPaymentId(payment.getId());
        response.setTuitionId(payment.getTuitionFee().getId());
        response.setTransactionCode(payment.getTransactionCode());
        response.setAmount(payment.getAmount());
        response.setMethod(payment.getMethod());
        response.setPaymentStatus(payment.getStatus());
        response.setPaidAt(payment.getPaidAt());
        response.setCreatedAt(payment.getCreatedAt());

        TuitionInfo tuitionInfo = new TuitionInfo();
        tuitionInfo.setSemesterId(payment.getTuitionFee().getSemester().getId());
        tuitionInfo.setSemesterName(payment.getTuitionFee().getSemester().getName().name());
        tuitionInfo.setFinalAmount(payment.getTuitionFee().getFinalAmount());
        tuitionInfo.setDueDate(payment.getTuitionFee().getDueDate());
        tuitionInfo.setTuitionStatus(payment.getTuitionFee().getStatus());
        response.setTuition(tuitionInfo);

        return response;
    }
}
