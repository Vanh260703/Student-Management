package com.example.quan_ly_sinh_vien_v2.DTO.Request.Class;

import com.example.quan_ly_sinh_vien_v2.Modal.Enum.ClassStatus;
import lombok.Getter;

@Getter
public class CreateClassRequest {
    private Long semesterId;
    private Long subjectId;
    private Long teacherId;
    private String classCode;
    private Integer maxStudents;
    private String room;
}
