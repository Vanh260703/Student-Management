package com.example.quan_ly_sinh_vien_v2.DTO.Request.Department;

import lombok.Getter;

@Getter
public class UpdateDepartmentRequest {
    private String name;
    private String description;
    private Long headTeacherId;
}
