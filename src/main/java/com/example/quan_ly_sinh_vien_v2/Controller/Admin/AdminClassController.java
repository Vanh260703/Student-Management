package com.example.quan_ly_sinh_vien_v2.Controller.Admin;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.Admin.UpdateClassRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.APIResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.ClassResponse;
import com.example.quan_ly_sinh_vien_v2.Service.Admin.AdminClassService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/admin/classes")
@PreAuthorize("hasRole('ADMIN')")
public class AdminClassController {
    private final AdminClassService adminClassService;

    public AdminClassController(AdminClassService adminClassService) {
        this.adminClassService = adminClassService;
    }

    // [PUT] /api/v2/admin/classes/:classId
    @PutMapping("/{classId}")
    public ResponseEntity<?> updateClass(
            @PathVariable Long classId,
            @Valid @RequestBody UpdateClassRequest request
    ) {
        ClassResponse classResponse = adminClassService.updateClass(classId, request);

        APIResponse response = new APIResponse(
                200,
                "Update class success!",
                classResponse
        );

        return ResponseEntity.ok(response);
    }
}
