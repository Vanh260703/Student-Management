package com.example.quan_ly_sinh_vien_v2.DTO.Request.Admin;

import lombok.Getter;

@Getter
public class CreateTeacherRequest {
    private String fullName;
    private String personalEmail;
    private String phone;
    private String avatarUrl;
    private String teacherCode;
    private Long departmentId;
    private String degree;
    private String address;
}
