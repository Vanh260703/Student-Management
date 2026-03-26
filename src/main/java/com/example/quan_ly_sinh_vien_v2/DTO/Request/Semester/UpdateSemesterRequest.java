package com.example.quan_ly_sinh_vien_v2.DTO.Request.Semester;

import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
public class UpdateSemesterRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private Instant registrationStart;
    private Instant registrationEnd;
}
