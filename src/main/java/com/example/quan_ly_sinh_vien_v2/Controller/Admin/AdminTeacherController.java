package com.example.quan_ly_sinh_vien_v2.Controller.Admin;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.Admin.CreateTeacherRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.APIResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.TeacherResponse;
import com.example.quan_ly_sinh_vien_v2.Service.Admin.AdminTeacherService;
import jakarta.mail.Multipart;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v2/admin/teachers")
@PreAuthorize("hasRole('ADMIN')")
public class AdminTeacherController {
    private final AdminTeacherService adminTeacherService;

    public AdminTeacherController(AdminTeacherService adminTeacherService) {
        this.adminTeacherService = adminTeacherService;
    }

    // [GET] /api/v2/admin/teachers
    @GetMapping()
    public ResponseEntity<?> teachers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String degree
    ) {
        List<TeacherResponse> teachers = adminTeacherService.getTeachers(search, departmentId, degree);

        APIResponse response = new APIResponse(
                200,
                "Get teachers success!",
                teachers
        );

        return ResponseEntity.ok(response);
    }

    // [POST] /api/v2/admin/teachers
    @PostMapping()
    public ResponseEntity<?> createTeacher(@Valid @RequestBody CreateTeacherRequest request) {
        TeacherResponse teacher = adminTeacherService.createTeacher(request);

        APIResponse response = new APIResponse(
                200,
                "Create teacher success!",
                teacher
        );

        return ResponseEntity.ok(response);
    }

    // [POST] /api/v2/admin/teachers/bulk-import
    @PostMapping("/bulk-import")
    public ResponseEntity<?> importTeachers(@RequestParam("file") MultipartFile file) {
        adminTeacherService.importTeachers(file);

        APIResponse response = new APIResponse(
                200,
                "Import teachers success!",
                null
        );

        return ResponseEntity.ok(response);
    }

    // [GET] /api/v2/admin/teachers/:teacherId
    @GetMapping("/{teacherId}")
    public ResponseEntity<?> detailsTeacher(@PathVariable Long teacherId) {
        TeacherResponse teacher = adminTeacherService.getDetailsTeacher(teacherId);

        APIResponse response = new APIResponse(
                200,
                "Get details teacher success!",
                teacher
        );

        return ResponseEntity.ok(response);
    }
}
