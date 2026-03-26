package com.example.quan_ly_sinh_vien_v2.DTO.Response;

import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Semester;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.SemesterName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SemesterResponse {
    private Boolean isActive;
    private SemesterName name;
    private String academicYear;

    public static SemesterResponse from (Semester semester) {
        SemesterResponse response = new SemesterResponse();

        response.setIsActive(semester.getIsActive());
        response.setName(semester.getName());
        response.setAcademicYear(semester.getAcademicYear().getName());

        return response;
    }
}
