package com.example.quan_ly_sinh_vien_v2.DTO.Response;

import com.example.quan_ly_sinh_vien_v2.Modal.Entity.TuitionFee;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.TuitionStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class StudentTuitionResponse {
    private Long id;
    private SemesterInfo semester;
    private Double amount;
    private Double discount;
    private Double finalAmount;
    private LocalDate dueDate;
    private TuitionStatus status;
    private LocalDateTime createdAt;

    @Getter
    @Setter
    public static class SemesterInfo {
        private Long id;
        private String name;
        private Integer semesterNumber;
        private String academicYear;
    }

    public static StudentTuitionResponse from(TuitionFee tuitionFee) {
        StudentTuitionResponse response = new StudentTuitionResponse();
        response.setId(tuitionFee.getId());
        response.setAmount(tuitionFee.getAmount());
        response.setDiscount(tuitionFee.getDiscount());
        response.setFinalAmount(tuitionFee.getFinalAmount());
        response.setDueDate(tuitionFee.getDueDate());
        response.setStatus(tuitionFee.getStatus());
        response.setCreatedAt(tuitionFee.getCreatedAt());

        SemesterInfo semesterInfo = new SemesterInfo();
        semesterInfo.setId(tuitionFee.getSemester().getId());
        semesterInfo.setName(tuitionFee.getSemester().getName().name());
        semesterInfo.setSemesterNumber(tuitionFee.getSemester().getSemesterNumber());
        semesterInfo.setAcademicYear(tuitionFee.getSemester().getAcademicYear().getName());
        response.setSemester(semesterInfo);

        return response;
    }
}
