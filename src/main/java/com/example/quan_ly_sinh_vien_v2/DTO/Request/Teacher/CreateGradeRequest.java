package com.example.quan_ly_sinh_vien_v2.DTO.Request.Teacher;

import lombok.Getter;

@Getter
public class CreateGradeRequest {
    private Long enrollmentId;
    private Long componentId;
    private Double score;
}
