package com.example.quan_ly_sinh_vien_v2.DTO.Request.Semester;

import com.example.quan_ly_sinh_vien_v2.Modal.Enum.SemesterName;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
public class CreateSemesterRequest {
    private Long academicYearId;
    private Integer semesterNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private Instant registrationStart;
    private Instant registrationEnd;
    private Boolean isActive;
}
