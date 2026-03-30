package com.example.quan_ly_sinh_vien_v2.DTO.Response.Dashboard;

import com.example.quan_ly_sinh_vien_v2.DTO.Response.StudentProfileResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.StudentTuitionResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.Grade.StudentGradeResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Student Dashboard Response
 * Cung cấp thông tin cá nhân sinh viên, điểm số, lịch học, học phí, v.v.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentDashboardResponse {
    // Thông tin sinh viên
    private StudentProfileResponse studentInfo;

    // Thống kê học tập
    private Integer totalEnrolledClasses;
    private Integer totalCompletedCredits;
    private Double currentGPA;
    private String studentStatus;

    // Thống kê điểm số
    private Double averageScore;
    private Integer totalPassedSubjects;
    private Integer totalFailedSubjects;
    private List<StudentGradeResponse> recentGrades;

    // Thống kê tham dự
    private Double attendanceRate;
    private Integer totalAbsentDays;
    private Integer totalLateArrivals;

    // Thông tin học phí
    private StudentTuitionResponse tuitionInfo;
    private Double totalTuitionFee;
    private Double paidAmount;
    private Double remainingAmount;
    private String tuitionStatus;

    // Thông tin lịch học sắp tới
    private Integer upcomingClasses;
    private String nextClassName;
    private String nextClassRoom;
    private LocalDateTime nextClassTime;

    // Thông tin cơ bản
    private String programName;
    private String departmentName;
    private Integer enrollmentYear;

    // Thông tin cập nhật
    private LocalDateTime lastUpdated;
}

