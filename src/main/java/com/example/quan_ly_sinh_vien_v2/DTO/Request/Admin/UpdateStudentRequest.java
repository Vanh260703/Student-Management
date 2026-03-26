package com.example.quan_ly_sinh_vien_v2.DTO.Request.Admin;

import com.example.quan_ly_sinh_vien_v2.Modal.Enum.Gender;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.StudentStatus;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UpdateStudentRequest {

    private String fullName;
    private String phone;
    private String personalEmail;
    private LocalDate dayOfBirth;
    private String address;
    private Gender gender;
    private Boolean isActive;

    private Integer enrollmentYear;
    private String className;
    private StudentStatus status;
}
