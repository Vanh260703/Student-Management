package com.example.quan_ly_sinh_vien_v2.DTO.Response.Grade;

import com.example.quan_ly_sinh_vien_v2.Modal.Enum.GradeComponentType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class StudentGradeResponse {
    private Long enrollmentId;

    private String studentCode;

    private String name;

    private Map<GradeComponentType, Double> grades;
}
