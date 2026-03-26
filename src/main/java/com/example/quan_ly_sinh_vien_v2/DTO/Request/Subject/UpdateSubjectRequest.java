package com.example.quan_ly_sinh_vien_v2.DTO.Request.Subject;

import lombok.Getter;

@Getter
public class UpdateSubjectRequest {
    private String name;
    private String description;
    private Boolean isActive;
    private Integer credits;
}
