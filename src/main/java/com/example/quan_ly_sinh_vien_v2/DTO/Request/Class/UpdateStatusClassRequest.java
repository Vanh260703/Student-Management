package com.example.quan_ly_sinh_vien_v2.DTO.Request.Class;

import com.example.quan_ly_sinh_vien_v2.Modal.Enum.ClassStatus;
import lombok.Getter;

@Getter
public class UpdateStatusClassRequest {
    private ClassStatus status;
}
