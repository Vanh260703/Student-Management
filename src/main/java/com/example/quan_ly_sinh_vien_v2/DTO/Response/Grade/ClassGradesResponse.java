package com.example.quan_ly_sinh_vien_v2.DTO.Response.Grade;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ClassGradesResponse {
    private Long classId;
    private List<StudentGradeResponse> students;
}
