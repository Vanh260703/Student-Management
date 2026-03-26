package com.example.quan_ly_sinh_vien_v2.Controller;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.Teacher.*;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.APIResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.Attendance.AttendanceResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.ClassResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.Grade.ClassGradesResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.Grade.StudentGradeResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.GradeComponentResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.TeacherProfileResponse;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.GradeComponent;
import com.example.quan_ly_sinh_vien_v2.Service.TeacherService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v2/teacher")
@PreAuthorize("hasRole('TEACHER')")
public class TeacherController {
    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    // [PUT] /api/v2/teacher/classes/:classId
    @PutMapping("/{classId}")
    public ResponseEntity<?> updateClass(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long classId,
            @Valid @RequestBody UpdateClassRequest request
            ) {
        ClassResponse classResponse = teacherService.updateClass(userDetails.getUsername(), classId, request);

        APIResponse response = new APIResponse(
                200,
                "Update class success!",
                classResponse
        );

        return ResponseEntity.ok(response);
    }

    // [GET] /api/v2/teacher/classes
    @GetMapping("/classes")
    public ResponseEntity<?> getClasses(@AuthenticationPrincipal UserDetails userDetails) {
        List<ClassResponse> classes = teacherService.getClasses(userDetails.getUsername());

        APIResponse response = new APIResponse(
                200,
                "Get classes success!",
                classes
        );

        return ResponseEntity.ok(response);
    }

    // [GET] /api/v2/teacher/classes/:classId/attendance
    @GetMapping("/classes/{classId}/attendance")
    public ResponseEntity<?> getAttendance(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long classId,
            @RequestParam LocalDate date
    ) {
        AttendanceResponse attendanceResponse = teacherService.getAttendance(userDetails.getUsername(), classId, date);

        APIResponse response = new APIResponse(
                200,
                "Get attendance in class success!",
                attendanceResponse
        );

        return ResponseEntity.ok(response);
    }

    // [POST] /api/v2/teacher/classes/:classId/attendance
    @PostMapping("/classes/{classId}/attendace")
    public ResponseEntity<?> attendanceStudents(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long classId,
            @RequestParam LocalDate date,
            @Valid @RequestBody List<UpdateStatusAttendanceRequest> requests
    ) {
        AttendanceResponse attendanceResponse = teacherService.attendanceStudents(userDetails.getUsername(), classId, date, requests);

        APIResponse response = new APIResponse(
                200,
                "Update attendance success!",
                attendanceResponse
        );

        return ResponseEntity.ok(response);
    }

    // [PATCH] /api/v2/teacher/classes/:classId/attendance/:attendanceId
    @PatchMapping("/classes/{classId}/attendance/{attendanceId}")
    public ResponseEntity<?> attendanceStudent(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long classId,
            @PathVariable Long attendanceId,
            @RequestParam LocalDate date,
            @Valid @RequestBody UpdateStatusAttendanceRequest request
    ) {
        teacherService.attendanceStudent(userDetails.getUsername(), classId, attendanceId, date, request);

        APIResponse response = new APIResponse(
                200,
                "Update attendance success!",
                null
        );

        return ResponseEntity.ok(response);
    }

    // [GET] /api/v2/teacher/classes/:classId/grade-components
    @GetMapping("/classes/{classId}/grade-components")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<?> gradeComponents(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long classId
    ) {
        List<GradeComponent> gradeComponents = teacherService.gradeComponents(userDetails.getUsername(), classId);

        APIResponse response = new APIResponse(
                200,
                "Get grade components success!",
                gradeComponents
        );

        return ResponseEntity.ok(response);
    }

    // [POST] /api/v2/teacher/classes/:classId/grade-components
    @PostMapping("/classes/{classId}/grade-components")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<?> createGradeComponent(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long classId,
            @Valid @RequestBody CreateGradeComponentRequest request
    ) {
        GradeComponentResponse gradeComponent = teacherService.createGradeComponent(userDetails.getUsername(), classId, request);

        APIResponse response = new APIResponse(
                200,
                "Create grade components success!",
                gradeComponent
        );

        return ResponseEntity.ok(response);
    }

    // [PUT] /api/v2/teacher/classes/:classId/grade-components/:gradeComponentId
    @PutMapping("/classes/{classId}/grade-components/{gradeComponentId}")
    public ResponseEntity<?> updateGradeComponent(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long classId,
            @PathVariable Long gradeComponentId,
            @Valid @RequestBody UpdateGradeComponentRequest request
    ) {
        GradeComponentResponse gradeComponent = teacherService.updateGradeComponent(userDetails.getUsername(), classId, gradeComponentId, request);

        APIResponse response = new APIResponse(
                200,
                "Update grade component success!",
                gradeComponent
        );

        return ResponseEntity.ok(response);
    }

    // [DELETE] /api/v2/teacher/classes/:classId/grade-components/:gradeComponentId
    @DeleteMapping("/classes/{classId}/grade-components/{gradeComponentId}")
    public ResponseEntity<?> deleteGradeComponent(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long classId,
            @PathVariable Long gradeComponentId
    ) {
     teacherService.deleteGradeComponent(userDetails.getUsername(), classId, gradeComponentId);

        APIResponse response = new APIResponse(
                200,
                "Delete grade component success!",
                null
        );

        return ResponseEntity.ok(response);
    }

    // [GET] /api/v2/teacher/classes/:classId/grades
    @GetMapping("/classes/{classId}/grades")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<?> getGrades(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long classId,
            @RequestParam(required = false) Long componentId,
            @RequestParam(required = false) Boolean isPublished,
            @RequestParam(required = false) String search
    ) {
        ClassGradesResponse grades = teacherService.getGradesInClass(
                userDetails.getUsername(),
                classId,
                componentId,
                isPublished,
                search);

        APIResponse response = new APIResponse(
                200,
                "Get grades in class success!",
                grades
        );

        return ResponseEntity.ok(response);
    }

    // [POST] /api/v2/teacher/classes/:classId/grades
    @PostMapping("/classes/{classId}/grades")
    public ResponseEntity<?> createGrade(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long classId,
            @Valid @RequestBody CreateGradeRequest request
    ) {
        StudentGradeResponse grade = teacherService.createGrade(userDetails.getUsername(), classId, request);

        APIResponse response = new APIResponse(
                200,
                "Create grade for student success",
                grade
        );

        return ResponseEntity.ok(response);
    }

    // [PUT] /api/v2/teacher/classes/:classId/grades/:gradeId
    @PutMapping("/classes/{classId}/grades/{gradeId}")
    public ResponseEntity<?> updateGrade(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long classId,
            @PathVariable Long gradeId,
            @Valid @RequestBody UpdateGradeRequest request
    ) {
        StudentGradeResponse grade = teacherService.updateGrade(userDetails.getUsername(), classId, gradeId, request);

        APIResponse response = new APIResponse(
                200,
                "Update grade for student success",
                grade
        );

        return ResponseEntity.ok(response);
    }

    // [POST] /api/v2/teacher/classes/:classId/grades/import
    @PostMapping("/classes/{classId}/grades/import")
    public ResponseEntity<?> importGrades(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long classId,
            @RequestParam("file")MultipartFile file
            ) {
        List<StudentGradeResponse> grades = teacherService.importGrades(userDetails.getUsername(), classId, file);

        APIResponse response = new APIResponse(
                200,
                "Import grades for class success",
                grades
        );

        return ResponseEntity.ok(response);
    }

    // [PATCH] /api/v2/teacher/classes/:classId/grades/publish
    @PatchMapping("/classes/{classId}/grades/publish")
    public ResponseEntity<?> publishGrades(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long classId
    ) {
        int publishedCount = teacherService.publishGrades(userDetails.getUsername(), classId);

        APIResponse response = new APIResponse(
                200,
                "Publish grades for class success",
                publishedCount
        );

        return ResponseEntity.ok(response);
    }

    // [GET] /api/v2/teacher/profile
    @GetMapping("/profile")
    public ResponseEntity<?> myProfile(@AuthenticationPrincipal UserDetails userDetails) {
        TeacherProfileResponse result = teacherService.getMyProfile(userDetails.getUsername());

        APIResponse response = new APIResponse(
                200,
                "Get my profile success!",
                result
        );

        return ResponseEntity.ok(response);
    }

    // [PUT] /api/v2/teacher/profile
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        TeacherProfileResponse result = teacherService.updateProfile(userDetails.getUsername(), request);

        APIResponse response = new APIResponse(
                200,
                "Update profile success!",
                result
        );

        return ResponseEntity.ok(response);
    }
}
