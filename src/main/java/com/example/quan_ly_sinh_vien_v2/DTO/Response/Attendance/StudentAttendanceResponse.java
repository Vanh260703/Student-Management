package com.example.quan_ly_sinh_vien_v2.DTO.Response.Attendance;

import com.example.quan_ly_sinh_vien_v2.Modal.Enum.AttendanceStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentAttendanceResponse {
    private Long enrollmentId;
    private String studentCode;
    private String name;
    private AttendanceStatus status;
}
