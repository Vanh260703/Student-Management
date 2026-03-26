package com.example.quan_ly_sinh_vien_v2.DTO.Request.Teacher;

import com.example.quan_ly_sinh_vien_v2.Modal.Enum.GradeComponentType;
import lombok.Getter;

@Getter
public class CreateGradeComponentRequest {
    private Integer weight;
    private GradeComponentType type;
    private Double maxScore;
}
