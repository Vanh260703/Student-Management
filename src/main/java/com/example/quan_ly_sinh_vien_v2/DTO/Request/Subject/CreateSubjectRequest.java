package com.example.quan_ly_sinh_vien_v2.DTO.Request.Subject;

import lombok.Getter;

@Getter
public class CreateSubjectRequest {
    private Long departmentId;
    private String code;
    private String name;
    private Integer credits;
    private String description;
    private Boolean isActive;
}