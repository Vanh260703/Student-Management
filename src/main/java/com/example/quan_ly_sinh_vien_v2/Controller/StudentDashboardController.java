package com.example.quan_ly_sinh_vien_v2.Controller;

import com.example.quan_ly_sinh_vien_v2.DTO.Response.APIResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.Dashboard.StudentDashboardResponse;
import com.example.quan_ly_sinh_vien_v2.Service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Student Dashboard Controller
 * Cung cấp thông tin cá nhân, điểm số, lịch học, học phí cho Sinh viên
 */
@RestController
@RequestMapping("/api/v2/student/dashboard")
@PreAuthorize("hasRole('STUDENT')")
public class StudentDashboardController {
    private final DashboardService dashboardService;

    public StudentDashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * [GET] /api/v2/student/dashboard
     * Lấy tổng quát dashboard cho Sinh viên
     *
     * Thống kê của sinh viên:
     * - Thông tin cá nhân, chương trình, bộ môn, năm nhập học
     * - Số lớp đã đăng ký, tín chỉ tích lũy, GPA hiện tại, trạng thái
     * - Điểm số gần đây, số môn đạt/trượt
     * - Tỷ lệ tham dự, số ngày vắng, số lần muộn
     * - Thông tin học phí (tổng, đã thanh toán, còn lại, trạng thái)
     * - Thời khóa biểu sắp tới, lớp tiếp theo
     */
    @GetMapping
    public ResponseEntity<?> getStudentDashboard(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        StudentDashboardResponse dashboard = dashboardService.getStudentDashboard(userDetails.getUsername());

        APIResponse<StudentDashboardResponse> response = new APIResponse<>(
                200,
                "Get student dashboard successfully!",
                dashboard
        );

        return ResponseEntity.ok(response);
    }
}

