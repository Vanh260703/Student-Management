package com.example.quan_ly_sinh_vien_v2.DTO.Response;

import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Subject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubjectResponse {
    private String code;
    private String name;
    private Integer credits;
    private String departmentName;

    public static SubjectResponse from (Subject subject) {
        SubjectResponse response = new SubjectResponse();

        response.setCode(subject.getCode());
        response.setName(subject.getName());
        response.setCredits(subject.getCredits());
        response.setDepartmentName(subject.getDepartment().getName());

        return response;
    }
}
