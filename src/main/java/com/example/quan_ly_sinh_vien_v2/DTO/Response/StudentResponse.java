package com.example.quan_ly_sinh_vien_v2.DTO.Response;

import com.example.quan_ly_sinh_vien_v2.Modal.Entity.StudentProfile;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.Gender;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.StudentStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class StudentResponse {
    private Long id;
    private String studentCode;
    private Long departmentId;
    private String className;
    private String fullName;
    private String schoolEmail;
    private String personalEmail;
    private LocalDate dayOfBirth;
    private Gender gender;
    private String address;
    private String avatarUrl;
    private Float gpa;
    private Integer accumulatedCredits;
    private Integer enrollmentYear;
    private Long programId;
    private StudentStatus status;

    public static StudentResponse from (StudentProfile student) {
        StudentResponse response = new StudentResponse();

        response.setId(student.getId());
        response.setStudentCode(student.getStudentCode());
        response.setDepartmentId(student.getDepartment().getId());
        response.setClassName(student.getClassName());
        response.setFullName(student.getUser().getFullName());
        response.setSchoolEmail(student.getUser().getPersonalEmail());
        response.setDayOfBirth(student.getUser().getDateOfBirth());
        response.setGender(student.getUser().getGender());
        response.setAddress(student.getUser().getAddress());
        response.setAvatarUrl(student.getUser().getAvatarUrl());
        response.setGpa(student.getGpa());
        response.setAccumulatedCredits(student.getAccumulatedCredits());
        response.setProgramId(student.getProgram().getId());
        response.setEnrollmentYear(student.getEnrollmentYear());
        response.setStatus(student.getStatus());

        return response;
    }
}
