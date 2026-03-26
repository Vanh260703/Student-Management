package com.example.quan_ly_sinh_vien_v2.DTO.Response;

import com.example.quan_ly_sinh_vien_v2.Modal.Entity.StudentProfile;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.Gender;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.StudentStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class StudentProfileResponse {
    private UserInfo user;
    private AcademicInfo academic;

    @Getter
    @Setter
    public static class UserInfo {
        private Long id;
        private String email;
        private String fullName;
        private String phone;
        private String avatarUrl;
        private String personalEmail;
        private LocalDate dayOfBirth;
        private String address;
        private Gender gender;
    }

    @Getter
    @Setter
    public static class DepartmentInfo {
        private Long departmentId;
        private String name;
    }

    @Getter
    @Setter
    public static class ProgramInfo {
        private Long programId;
        private String name;
    }

    @Getter
    @Setter
    public static class AcademicInfo {
        private String studentCode;
        private ProgramInfo program;
        private DepartmentInfo department;
        private Integer enrollmentYear;
        private String className;
        private StudentStatus status;
    }

    public static StudentProfileResponse from (StudentProfile student) {
        StudentProfileResponse response = new StudentProfileResponse();

        StudentProfileResponse.UserInfo userInfo = new StudentProfileResponse.UserInfo();

        userInfo.setId(student.getId());
        userInfo.setEmail(student.getUser().getEmail());
        userInfo.setFullName(student.getUser().getFullName());
        userInfo.setPhone(student.getUser().getPhone());
        userInfo.setAvatarUrl(student.getUser().getAvatarUrl());
        userInfo.setPersonalEmail(student.getUser().getPersonalEmail());
        userInfo.setDayOfBirth(student.getUser().getDateOfBirth());
        userInfo.setAddress(student.getUser().getAddress());
        userInfo.setGender(student.getUser().getGender());

        StudentProfileResponse.DepartmentInfo departmentInfo = new StudentProfileResponse.DepartmentInfo();

        departmentInfo.setDepartmentId(student.getDepartment().getId());
        departmentInfo.setName(student.getDepartment().getName());

        StudentProfileResponse.ProgramInfo programInfo = new StudentProfileResponse.ProgramInfo();

        programInfo.setProgramId(student.getProgram().getId());
        programInfo.setName(student.getProgram().getName());

        StudentProfileResponse.AcademicInfo academicInfo = new StudentProfileResponse.AcademicInfo();

        academicInfo.setStudentCode(student.getStudentCode());
        academicInfo.setProgram(programInfo);
        academicInfo.setDepartment(departmentInfo);
        academicInfo.setClassName(student.getClassName());
        academicInfo.setEnrollmentYear(student.getEnrollmentYear());
        academicInfo.setStatus(student.getStatus());

        response.setUser(userInfo);
        response.setAcademic(academicInfo);

        return response;
    }
}
