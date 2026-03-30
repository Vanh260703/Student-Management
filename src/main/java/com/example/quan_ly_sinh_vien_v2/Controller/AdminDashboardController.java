package com.example.quan_ly_sinh_vien_v2.Controller;

import com.example.quan_ly_sinh_vien_v2.DTO.Response.APIResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.Dashboard.AdminDashboardResponse;
import com.example.quan_ly_sinh_vien_v2.Service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin Dashboard Controller
 * Cung cấp tổng quát tất cả đầy đủ của hệ thống cho Admin
 */
@RestController
@RequestMapping("/api/v2/admin/dashboard")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {
    private final DashboardService dashboardService;

    public AdminDashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * [GET] /api/v2/admin/dashboard
     * Lấy tổng quát dashboard cho Admin
     *
     * Thống kê toàn hệ thống:
     * - Số lượng sinh viên, giáo viên, quản trị viên
     * - Số lượng lớp học (mở, đóng)
     * - Số lượng môn học, chương trình, bộ môn, kỳ học
     * - Tổng học phí, số thanh toán
     * - Thống kê thông báo
     * - GPA trung bình, số sinh viên rớt
     */
    @GetMapping
    public ResponseEntity<?> getAdminDashboard() {
        AdminDashboardResponse dashboard = dashboardService.getAdminDashboard();

        APIResponse<AdminDashboardResponse> response = new APIResponse<>(
                200,
                "Get admin dashboard successfully!",
                dashboard
        );

        return ResponseEntity.ok(response);
    }
}

