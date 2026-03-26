package com.example.quan_ly_sinh_vien_v2.Controller.Admin;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.Admin.CreateStudentRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Request.Admin.UpdateStudentRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.APIResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.StudentProfileResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.StudentResponse;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.StudentStatus;
import com.example.quan_ly_sinh_vien_v2.Service.Admin.AdminStudentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v2/admin/students")
@PreAuthorize("hasRole('ADMIN')")
public class AdminStudentController {
    private final AdminStudentService adminStudentService;

    public AdminStudentController(AdminStudentService adminStudentService) {
        this.adminStudentService = adminStudentService;
    }

    // [GET] /api/v2/admin/students
    @GetMapping()
    public ResponseEntity<?> students(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long programId,
            @RequestParam(required = false) Integer enrollmentYear,
            @RequestParam(required = false) StudentStatus status,
            @RequestParam(required = false) Float gpaMin,
            @RequestParam(required = false) Float gpaMax
            ) {
        List<StudentResponse> students = adminStudentService.getStudents(search, departmentId, programId, enrollmentYear, status, gpaMin, gpaMax);

        APIResponse response = new APIResponse(
                200,
                "Get students success",
                students
        );

        return ResponseEntity.ok(response);
    }

    // [GET] /api/v2/admin/students/:studentId
    @GetMapping("/{studentId}")
    public ResponseEntity<?> detailsStudent(@PathVariable Long studentId) {
        StudentResponse student = adminStudentService.getDetailsStudent(studentId);

        APIResponse response = new APIResponse(
                200,
                "Get details student success",
                student
        );

        return ResponseEntity.ok(response);
    }

    // [POST] /api/v2/admin/students
    @PostMapping()
    public ResponseEntity<?> createStudent(@Valid @RequestBody CreateStudentRequest request) {
        StudentResponse student = adminStudentService.createStudent(request);

        APIResponse response = new APIResponse(
                200,
                "Create student success",
                student
        );

        return ResponseEntity.ok(response);
    }

    // [POST] /api/v2/admin/students/bulk-import
    @PostMapping("/bulk-import")
    public ResponseEntity<?> importStudents(@RequestParam("file") MultipartFile file) {
         adminStudentService.importStudents(file);

        APIResponse response = new APIResponse(
                200,
                "Import students success",
                null
        );

        return ResponseEntity.ok(response);
    }

    // [PUT] /api/v2/admin/students/:studentId
    @PutMapping("/{studentId}")
    public ResponseEntity<?> updateStudent(
            @PathVariable Long studentId,
            @RequestBody UpdateStudentRequest request
    ) {
        StudentProfileResponse result = adminStudentService.updateStudent(studentId, request);

        APIResponse response = new APIResponse(
                200,
                "Update student success!",
                result
        );

        return ResponseEntity.ok(response);
    }
}
