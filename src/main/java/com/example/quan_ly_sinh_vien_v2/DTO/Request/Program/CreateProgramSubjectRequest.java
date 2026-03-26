package com.example.quan_ly_sinh_vien_v2.DTO.Request.Program;

import lombok.Getter;

@Getter
public class CreateProgramSubjectRequest {
    private Long subjectId;
    private Integer semester;
    private Boolean isRequired;
    private Long prerequisiteSubjectId;
}
