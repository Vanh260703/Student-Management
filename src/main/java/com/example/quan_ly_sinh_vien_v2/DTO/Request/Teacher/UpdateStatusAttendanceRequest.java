package com.example.quan_ly_sinh_vien_v2.DTO.Request.Teacher;

import com.example.quan_ly_sinh_vien_v2.Modal.Enum.AttendanceStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStatusAttendanceRequest {
    private Long enrollmentId;
    private AttendanceStatus status;
}
