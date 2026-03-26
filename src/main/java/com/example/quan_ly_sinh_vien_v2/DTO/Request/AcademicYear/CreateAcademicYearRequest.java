package com.example.quan_ly_sinh_vien_v2.DTO.Request.AcademicYear;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class CreateAcademicYearRequest {
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isCurrent;
}
