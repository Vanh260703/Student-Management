package com.example.quan_ly_sinh_vien_v2.DTO.Response.Dashboard;

import com.example.quan_ly_sinh_vien_v2.DTO.Response.ClassResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.TeacherProfileResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Teacher Dashboard Response
 * Cung cấp thông tin về lớp học, sinh viên, điểm số của giáo viên
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeacherDashboardResponse {
    // Thông tin giáo viên
    private TeacherProfileResponse teacherInfo;

    // Thống kê lớp học
    private Long totalClasses;
    private Long totalStudents;
    private List<ClassResponse> classes;

    // Thống kê học tập
    private Integer totalGradesPosted;
    private Integer totalGradesPending;
    private Long totalEnrollments;

    // Thống kê tham dự
    private Long totalAttendanceRecords;
    private Integer averageAttendanceRate;

    // Thống kê điểm
    private Double averageClassGPA;
    private Long totalFailedStudents;
    private Long totalExcellentStudents;

    // Thông tin về lớp được dạy nhiều nhất
    private String largestClassName;
    private Integer largestClassSize;

    // Thông tin về lớp được dạy ít nhất
    private String smallestClassName;
    private Integer smallestClassSize;

    // Thông tin cập nhật
    private LocalDateTime lastUpdated;
    private String departmentName;
}

