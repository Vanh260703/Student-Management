package com.example.quan_ly_sinh_vien_v2.Controller;

import com.example.quan_ly_sinh_vien_v2.DTO.Response.APIResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.ClassResponse;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Enrollment;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.User;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.EnrollmentStatus;
import com.example.quan_ly_sinh_vien_v2.Service.EnrollmentService;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/enrollments")
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    // [GET] /api/v2/enrollments/available-classes (STUDENT ONLY)
    @GetMapping("/available-classes")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> availableClasses(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long semesterId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Boolean hasSlot,
            @RequestParam(required = false) Boolean notEnrolled,
            @RequestParam(required = false) String search
    ) {
        List<ClassResponse> classes = enrollmentService.getAvailableClasses(userDetails.getUsername(), semesterId, subjectId, departmentId, hasSlot, notEnrolled, search);

        APIResponse response = new APIResponse(
                200,
                "Get available classes success!",
                classes
        );

        return ResponseEntity.ok(response);
    }

    // [POST] /api/v2/enrollments/:classId (STUDENT ONLY)
    @PostMapping("/{classId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> enrollInClass(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long classId
    ) {
        enrollmentService.enrollInClass(userDetails.getUsername(), classId);

        APIResponse response = new APIResponse(
                200,
                "Register subject success!",
                null
        );

        return ResponseEntity.ok(response);
    }

    // [DELETE] /api/v2/enrollments/:classId (STUDENT ONLY)
    @DeleteMapping("/{enrollmentId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> deleteEnrollment(@PathVariable Long enrollmentId) {
        enrollmentService.deleteEnrollment(enrollmentId);

        APIResponse response = new APIResponse(
                200,
                "Delete enrollment success!",
                null
        );

        return ResponseEntity.ok(response);
    }

    // [GET] /api/v2/enrollments/my (STUDENT ONLY)
    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> myEnrollments(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long semesterId,
            @RequestParam(required = false) EnrollmentStatus status
            ) {
        List<Enrollment> enrollments = enrollmentService.myEnrollments(userDetails.getUsername(), semesterId, status);

        APIResponse response = new APIResponse(
                200,
                "Get enrollments success!",
                enrollments
        );

        return ResponseEntity.ok(response);
    }

    // [GET] /api/v2/enrollments/:enrollmentId (ALL)
    @GetMapping("/{enrollmentId}")
    public ResponseEntity<?> detailsEnrollment(@PathVariable Long enrollmentId) {
        Enrollment enrollment = enrollmentService.getDetailsEnrollment(enrollmentId);

        APIResponse response = new APIResponse(
                200,
                "Get details enrollment success!",
                enrollment
        );

        return ResponseEntity.ok(response);
    }
}
