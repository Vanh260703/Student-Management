package com.example.quan_ly_sinh_vien_v2.DTO.Response.Attendance;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class AttendanceResponse {
    private Long classId;
    private LocalDate date;
    private List<StudentAttendanceResponse> students;
}
