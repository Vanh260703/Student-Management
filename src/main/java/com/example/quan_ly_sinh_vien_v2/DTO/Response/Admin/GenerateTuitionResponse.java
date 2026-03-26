package com.example.quan_ly_sinh_vien_v2.DTO.Response.Admin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenerateTuitionResponse {
    private Long semesterId;
    private String semesterName;
    private Integer creditPrice;
    private Integer totalEnrollments;
    private Integer totalStudents;
    private Integer generatedCount;
    private Integer skippedCount;
}
