package com.example.quan_ly_sinh_vien_v2.Controller;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.Department.CreateDepartmentRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Request.Department.UpdateDepartmentRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.APIResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.StudentResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.TeacherResponse;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Department;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.StudentStatus;
import com.example.quan_ly_sinh_vien_v2.Service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/departments")
public class DepartmentController {
    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    // [POST] api/v2/departments
    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createDepartment(@Valid @RequestBody CreateDepartmentRequest request) {
        Department department = departmentService.createDepartment(request);

        APIResponse response = new APIResponse(
                200,
                "Create department success!",
                department
        );

        return ResponseEntity.ok(response);
    }

    // [GET] api/v2/departments/:departmentId
    @GetMapping("/{departmentId}")
    public ResponseEntity<?> detailsDepartment(@PathVariable Long departmentId) {
        Department department = departmentService.getDetailsDepartment(departmentId);

        APIResponse response = new APIResponse(
                200,
                "Get details department success!",
                department
        );

        return ResponseEntity.ok(response);
    }

    // [PUT] api/v2/departments/:departmentId
    @PutMapping("/{departmentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateDepartment(
            @PathVariable Long departmentId,
            @Valid @RequestBody UpdateDepartmentRequest request) {
        Department department = departmentService.updateDepartment(departmentId, request);

        APIResponse response = new APIResponse(
                200,
                "Update department success!",
                department
        );

        return ResponseEntity.ok(response);
    }

    // [DELETE] api/v2/departments/:departmentId
    @DeleteMapping("/{departmentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteDepartment(@PathVariable Long departmentId) {
        departmentService.deleteDepartment(departmentId);

        APIResponse response = new APIResponse(
                200,
                "Delete department success!",
                null
        );

        return ResponseEntity.ok(response);
    }

    // [GET] api/v2/departments/:departmentId/teachers
    @GetMapping("/{departmentId}/teachers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> teachersInDepartment(
            @PathVariable Long departmentId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String degree) {
        List<TeacherResponse> teachers = departmentService.getTeachersInDepartment(departmentId, search, degree);

        APIResponse response = new APIResponse(
                200,
                "Get teachers in department success!",
                teachers
        );

        return ResponseEntity.ok(response);
    }

    // [GET] api/v2/departments/:departmentId/students
    @GetMapping("/{departmentId}/students")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> studentsInDepartment(
            @PathVariable Long departmentId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long programId,
            @RequestParam(required = false) Integer enrollmentYear,
            @RequestParam(required = false)StudentStatus status
            ) {
        List<StudentResponse> students = departmentService.getStudentsInDepartment(departmentId, search, programId, enrollmentYear, status);

        APIResponse response = new APIResponse(
                200,
                "Get students in department success!",
                students
        );

        return ResponseEntity.ok(response);
    }
}
