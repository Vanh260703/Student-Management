package com.example.quan_ly_sinh_vien_v2.DTO.Request.Student;

import com.example.quan_ly_sinh_vien_v2.Modal.Enum.Gender;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UpdateProfileRequest {
    private String fullName;
    private String phone;
    private String personalEmail;
    private LocalDate dayOfBirth;
    private String address;
    private Gender gender;
}
