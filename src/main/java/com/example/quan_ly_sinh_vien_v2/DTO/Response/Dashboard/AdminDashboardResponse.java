package com.example.quan_ly_sinh_vien_v2.DTO.Response.Dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Admin Dashboard Response
 * Cung cấp tổng quát toàn bộ hệ thống
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminDashboardResponse {
    // Thống kê người dùng
    private Long totalStudents;
    private Long totalTeachers;
    private Long totalAdmins;
    private Long totalActiveUsers;
    private Long totalInactiveUsers;

    // Thống kê lớp học
    private Long totalClasses;
    private Long totalOpenClasses;
    private Long totalClosedClasses;
    private Long totalEnrollments;

    // Thống kê học tập
    private Long totalSubjects;
    private Long totalPrograms;
    private Long totalDepartments;
    private Long totalSemesters;

    // Thống kê tài chính
    private Double totalTuitionCollected;
    private Double totalTuitionPending;
    private Long totalPaymentTransactions;
    private Long totalPaidPayments;
    private Long totalPendingPayments;

    // Thống kê thông báo
    private Long totalNotificationsSent;
    private Long totalUnreadNotifications;

    // Thống kê nâng cao
    private Integer averageStudentsPerClass;
    private Double averageGPA;
    private Long totalFailedGrades;

    // Thông tin cập nhật
    private LocalDateTime lastUpdated;
    private String systemStatus;
}

