package com.example.quan_ly_sinh_vien_v2.DTO.Request.Admin;

import com.example.quan_ly_sinh_vien_v2.Modal.Enum.EnrollmentStatus;
import lombok.Getter;

@Getter
public class UpdateStatusEnrollment {
    private EnrollmentStatus status;
}
