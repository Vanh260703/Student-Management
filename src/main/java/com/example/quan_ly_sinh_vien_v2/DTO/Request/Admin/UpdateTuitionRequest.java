package com.example.quan_ly_sinh_vien_v2.DTO.Request.Admin;

import com.example.quan_ly_sinh_vien_v2.Modal.Enum.TuitionStatus;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UpdateTuitionRequest {
    private Double amount;
    private Double discount;
    private LocalDate dueDate;
    private TuitionStatus status;
}
