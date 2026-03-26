package com.example.quan_ly_sinh_vien_v2.DTO.Response;

import com.example.quan_ly_sinh_vien_v2.Modal.Entity.ClassEntity;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.ClassStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClassResponse {
    private String classCode;
    private SemesterResponse semesterResponse;
    private ClassStatus status;
    private SubjectResponse subjectResponse;
    private TeacherResponse teacherResponse;

    public static ClassResponse from (ClassEntity classEntity) {
        ClassResponse response = new ClassResponse();

        response.setClassCode(classEntity.getClassCode());
        response.setSemesterResponse(SemesterResponse.from(classEntity.getSemester()));
        response.setSubjectResponse(SubjectResponse.from(classEntity.getSubject()));
        response.setTeacherResponse(TeacherResponse.from(classEntity.getTeacher()));
        response.setStatus(classEntity.getStatus());

        return response;
    }
}
