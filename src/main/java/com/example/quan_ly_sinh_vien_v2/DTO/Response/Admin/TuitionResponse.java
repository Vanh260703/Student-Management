package com.example.quan_ly_sinh_vien_v2.DTO.Response.Admin;

import com.example.quan_ly_sinh_vien_v2.Modal.Entity.StudentProfile;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.TuitionFee;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.TuitionStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class TuitionResponse {
    private Long id;
    private StudentInfo student;
    private SemesterInfo semester;
    private Double amount;
    private Double discount;
    private Double finalAmount;
    private LocalDate dueDate;
    private TuitionStatus status;
    private LocalDateTime createdAt;

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
    public static class SemesterInfo {
        private Long id;
        private String name;
    }

    public static TuitionResponse from(TuitionFee tuitionFee, StudentProfile studentProfile) {
        TuitionResponse response = new TuitionResponse();
        response.setId(tuitionFee.getId());
        response.setAmount(tuitionFee.getAmount());
        response.setDiscount(tuitionFee.getDiscount());
        response.setFinalAmount(tuitionFee.getFinalAmount());
        response.setDueDate(tuitionFee.getDueDate());
        response.setStatus(tuitionFee.getStatus());
        response.setCreatedAt(tuitionFee.getCreatedAt());

        StudentInfo studentInfo = new StudentInfo();
        studentInfo.setId(tuitionFee.getStudent().getId());
        studentInfo.setFullName(tuitionFee.getStudent().getFullName());
        studentInfo.setEmail(tuitionFee.getStudent().getEmail());
        studentInfo.setStudentCode(studentProfile != null ? studentProfile.getStudentCode() : null);
        response.setStudent(studentInfo);

        SemesterInfo semesterInfo = new SemesterInfo();
        semesterInfo.setId(tuitionFee.getSemester().getId());
        semesterInfo.setName(tuitionFee.getSemester().getName().name());
        response.setSemester(semesterInfo);

        return response;
    }
}
