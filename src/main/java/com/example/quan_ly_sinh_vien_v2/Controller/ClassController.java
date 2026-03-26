package com.example.quan_ly_sinh_vien_v2.Controller;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.Class.CreateClassRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Request.Class.CreateClassScheduleRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Request.Class.UpdateStatusClassRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.APIResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.ClassResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.ClassScheduleResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.StudentResponse;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.ClassEntity;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.ClassStatus;
import com.example.quan_ly_sinh_vien_v2.Service.ClassService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/classes")
public class ClassController {
    private final ClassService classService;

    public ClassController(ClassService classService) {
        this.classService = classService;
    }

    // [GET] /api/v2/classes (ALL)
    @GetMapping()
    public ResponseEntity<?> classes(
            @RequestParam(required = false) Long semesterId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Long teacherId,
            @RequestParam(required = false) ClassStatus status,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean hasSlot
            ) {
        List<ClassResponse> classes = classService.getClasses(semesterId, subjectId, teacherId, status, search, hasSlot);

        APIResponse response = new APIResponse(
                200,
                "Get classes success!",
                classes
        );

        return ResponseEntity.ok(response);
    }

    // [POST] /api/v2/classes (ADMIN ONLY)
    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createClass(@Valid @RequestBody CreateClassRequest request) {
        ClassEntity classEntity = classService.createClass(request);

        APIResponse response = new APIResponse(
                200,
                "Create classes success!",
                classEntity
        );

        return ResponseEntity.ok(response);
    }

    // [GET] /api/v2/classes/:classId (ALL)
    @GetMapping("/{classId}")
    public ResponseEntity<?> getDetailsClass(@PathVariable Long classId) {
        ClassResponse classResponse = classService.getDetailsClass(classId);

        APIResponse response = new APIResponse(
                200,
                "Get details class success!",
                classResponse
        );

        return ResponseEntity.ok(response);
    }

    // [PATCH] /api/v2/classes/:classId/change-status (ADMIN ONLY)
    @PatchMapping("/{classId}/change-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long classId,
            @Valid @RequestBody UpdateStatusClassRequest request) {
        classService.updateStatus(classId, request);

        APIResponse response = new APIResponse(
                200,
                "Update class status success!",
                null
        );

        return ResponseEntity.ok(response);
    }

    // [DELETE] /api/v2/classes/:classId (ADMIN ONLY)
    @DeleteMapping("/{classId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteClass(@PathVariable Long classId) {
        classService.deleteClass(classId);

        APIResponse response = new APIResponse(
                200,
                "Delete class success!",
                null
        );

        return ResponseEntity.ok(response);
    }

    // [GET] /api/v2/classes/:classId/students (ADMIN/TEACHER)
    @GetMapping("/{classId}/student")
    @PreAuthorize("hasAllRoles('ADMIN', 'TEACHER')")
    public ResponseEntity<?> studentsInClass(
            @PathVariable Long classId,
            @RequestParam(required = false) String search) {
        List<StudentResponse> students = classService.getStudentsInClass(classId, search);

        APIResponse response = new APIResponse(
                200,
                "Get students in class success!",
                students
        );

        return ResponseEntity.ok(response);
    }

    // [POST] /api/v2/classes/:classId/schedules (ADMIN)
    @PostMapping("/{classId}/schedules")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createSchedule(
            @PathVariable Long classId,
            @Valid @RequestBody CreateClassScheduleRequest request
    ) {
        ClassScheduleResponse result = classService.createSchedule(classId, request);

        APIResponse response = new APIResponse(
                200,
                "Create schedules success!",
                result
        );

        return ResponseEntity.ok(response);
    }

    // [GET] /api/v2/classes/:classId/schedules (ALL)
    @GetMapping("/{classId}/schedules")
    public ResponseEntity<?> getSchedules(@PathVariable Long classId) {
        ClassScheduleResponse result = classService.getSchedules(classId);

        APIResponse response = new APIResponse(
                200,
                "Get schedules in class success!",
                result
        );

        return ResponseEntity.ok(response);
    }
}
