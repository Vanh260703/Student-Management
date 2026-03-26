package com.example.quan_ly_sinh_vien_v2.DTO.Response;

import com.example.quan_ly_sinh_vien_v2.Modal.Entity.TeacherProfile;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.Gender;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TeacherResponse {
    private Long id;
    private String teacherCode;
    private String fullName;
    private String schoolEmail;
    private String personalEmail;
    private LocalDate dayOfBirth;
    private Gender gender;
    private String address;
    private String avatarUrl;
    private Long departmentId;
    private String degree;
    private String specialization;
    private LocalDate joinedDate;

    public static TeacherResponse from (TeacherProfile teacher) {
        TeacherResponse response = new TeacherResponse();

        response.setId(teacher.getId());
        response.setTeacherCode(teacher.getTeacherCode());
        response.setFullName(teacher.getUser().getFullName());
        response.setSchoolEmail(teacher.getUser().getEmail());
        response.setPersonalEmail(teacher.getUser().getPersonalEmail());
        response.setDayOfBirth(teacher.getUser().getDateOfBirth());
        response.setGender(teacher.getUser().getGender());
        response.setAddress(teacher.getUser().getAddress());
        response.setAvatarUrl(teacher.getUser().getAvatarUrl());
        response.setDegree(teacher.getDegree());
        response.setDepartmentId(teacher.getDepartment().getId());
        response.setSpecialization(teacher.getSpecialization());
        response.setJoinedDate(teacher.getJoinedDate());

        return response;
    }
}
