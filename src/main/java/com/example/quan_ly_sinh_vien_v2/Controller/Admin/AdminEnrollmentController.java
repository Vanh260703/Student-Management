package com.example.quan_ly_sinh_vien_v2.Controller.Admin;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.Admin.UpdateStatusEnrollment;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.APIResponse;
import com.example.quan_ly_sinh_vien_v2.Service.Admin.AdminEnrollmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/admin/enrollments")
@PreAuthorize("hasRole('ADMIN')")
public class AdminEnrollmentController {
    private final AdminEnrollmentService adminEnrollmentService;

    public AdminEnrollmentController(AdminEnrollmentService adminEnrollmentService) {
        this.adminEnrollmentService = adminEnrollmentService;
    }

    // [PATCH] /api/v2/admin/enrollments/:enrollmentId
    @GetMapping("/{enrollmentId}")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long enrollmentId,
            @Valid @RequestBody UpdateStatusEnrollment request) {
        adminEnrollmentService.updateStatus(enrollmentId, request);

        APIResponse response = new APIResponse(
                200,
                "Update status success",
                null
        );

        return ResponseEntity.ok(response);
    }
}
