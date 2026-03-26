package com.example.quan_ly_sinh_vien_v2.DTO.Request.Program;

import lombok.Getter;
import lombok.Setter;

@Getter
public class CreateProgramRequest {
    private Long departmentId;
    private String code;
    private String name;
    private Integer totalCredits;
    private Integer durationYear;
    private String description;
}
