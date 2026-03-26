package com.example.quan_ly_sinh_vien_v2.DTO.Request.Program;

import lombok.Getter;

@Getter
public class UpdateProgramRequest {
    private String name;
    private Integer totalCredits;
    private String description;
    private Integer durationYear;
}
