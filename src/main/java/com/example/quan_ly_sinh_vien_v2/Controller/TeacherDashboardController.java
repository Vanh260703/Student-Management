package com.example.quan_ly_sinh_vien_v2.Controller;

import com.example.quan_ly_sinh_vien_v2.DTO.Response.APIResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.Dashboard.TeacherDashboardResponse;
import com.example.quan_ly_sinh_vien_v2.Service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Teacher Dashboard Controller
 * Cung cấp tổng quát lớp học, danh sách lớp, điểm số cho Giáo viên
 */
@RestController
@RequestMapping("/api/v2/teacher/dashboard")
@PreAuthorize("hasRole('TEACHER')")
public class TeacherDashboardController {
    private final DashboardService dashboardService;

    public TeacherDashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * [GET] /api/v2/teacher/dashboard
     * Lấy tổng quát dashboard cho Giáo viên
     *
     * Thống kê của giáo viên:
     * - Thông tin giáo viên, bộ môn
     * - Tổng số lớp dạy, số sinh viên
     * - Số lớp mở, danh sách lớp dạy
     * - Tổng số điểm đã cập nhật, đang chờ
     * - Tổng số đơn enrollments
     * - GPA trung bình lớp, số sinh viên rớt, xuất sắc
     * - Thông tin lớp lớn nhất và nhỏ nhất
     */
    @GetMapping
    public ResponseEntity<?> getTeacherDashboard(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        TeacherDashboardResponse dashboard = dashboardService.getTeacherDashboard(userDetails.getUsername());

        APIResponse<TeacherDashboardResponse> response = new APIResponse<>(
                200,
                "Get teacher dashboard successfully!",
                dashboard
        );

        return ResponseEntity.ok(response);
    }
}

