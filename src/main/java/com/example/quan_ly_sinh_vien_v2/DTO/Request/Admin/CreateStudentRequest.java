package com.example.quan_ly_sinh_vien_v2.DTO.Request.Admin;

import com.example.quan_ly_sinh_vien_v2.Modal.Enum.Gender;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class CreateStudentRequest {
    private String fullName;
    private String personalEmail;
    private String phone;
    private String avatarUrl;
    private Long departmentId;
    private Long programId;
    private LocalDate dayOfBirth;
    private String address;
    private Gender gender;
    private String className;
}
