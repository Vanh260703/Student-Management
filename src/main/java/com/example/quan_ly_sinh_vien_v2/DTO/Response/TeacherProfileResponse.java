package com.example.quan_ly_sinh_vien_v2.DTO.Response;

import com.example.quan_ly_sinh_vien_v2.Modal.Entity.TeacherProfile;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.Gender;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class TeacherProfileResponse {
    private Long id;
    private String teacherCode;

    private String fullName;
    private String email;
    private String personalEmail;
    private String phone;
    private String avatarUrl;
    private Gender gender;
    private LocalDate dateOfBirth;

    private DepartmentInfo department;

    private String degree;
    private LocalDate joinedDate;

    private Boolean isActive;

    @Getter
    @Setter
    public static class DepartmentInfo {
        private Long id;
        private String name;
    }

    public static TeacherProfileResponse from (TeacherProfile teacher) {
        DepartmentInfo departmentInfo = new DepartmentInfo();

        departmentInfo.setId(teacher.getDepartment().getId());
        departmentInfo.setName(teacher.getDepartment().getName());

        TeacherProfileResponse response = new TeacherProfileResponse();

        response.setId(teacher.getId());
        response.setTeacherCode(teacher.getTeacherCode());
        response.setFullName(teacher.getUser().getFullName());
        response.setEmail(teacher.getUser().getEmail());
        response.setPersonalEmail(teacher.getUser().getPersonalEmail());
        response.setPhone(teacher.getUser().getPhone());
        response.setAvatarUrl(teacher.getUser().getAvatarUrl());
        response.setGender(teacher.getUser().getGender());
        response.setDateOfBirth(teacher.getUser().getDateOfBirth());
        response.setDepartment(departmentInfo);
        response.setDegree(teacher.getDegree());
        response.setJoinedDate(teacher.getJoinedDate());
        response.setIsActive(teacher.getUser().getIsActive());

        return response;
    }
}
