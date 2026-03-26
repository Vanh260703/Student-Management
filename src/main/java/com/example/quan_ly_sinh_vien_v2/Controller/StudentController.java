package com.example.quan_ly_sinh_vien_v2.Controller;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.Student.UpdateProfileRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.APIResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.AvatarUploadResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.Grade.AllGradeStudent;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.Grade.StudentGradeResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.ProgramResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.StudentPaymentHistoryResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.StudentProfileResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.StudentTimetableResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.StudentTuitionResponse;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.PaymentStatus;
import com.example.quan_ly_sinh_vien_v2.Service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping({"/api/v2/student", "/api/student"})
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // [GET] /api/v2/student/grades
    @GetMapping("/grades")
    public ResponseEntity<?> allGrades(@AuthenticationPrincipal UserDetails userDetails) {
        AllGradeStudent result = studentService.getAllGrades(userDetails.getUsername());

        APIResponse response = new APIResponse(
                200,
                "Get all grades success!",
                result
        );

        return ResponseEntity.ok(response);
    }

    // [GET] /api/v2/student/grades/:classId
    @GetMapping("/grades/{classId}")
    public ResponseEntity<?> gradesInClass(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long classId
    ) {
        StudentGradeResponse result = studentService.getGradesInClass(userDetails.getUsername(), classId);

        APIResponse response = new APIResponse(
                200,
                "Get grades in class success!",
                result
        );

        return ResponseEntity.ok(response);
    }

    // [GET] /api/v2/student/profile
    @GetMapping("/profile")
    public ResponseEntity<?> profile(@AuthenticationPrincipal UserDetails userDetails) {
        StudentProfileResponse result = studentService.getProfile(userDetails.getUsername());

        APIResponse response = new APIResponse(
                200,
                "Get profile success!",
                result
        );

        return ResponseEntity.ok(response);
    }

    // [PUT] /api/v2/student/profile
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest request
            ) {
        StudentProfileResponse result = studentService.updateProfile(userDetails.getUsername(), request);

        APIResponse response = new APIResponse(
                200,
                "Update profile success!",
                result
        );

        return ResponseEntity.ok(response);
    }

    // [POST] /api/student/profile/upload-avatar
    @PostMapping("/profile/upload-avatar")
    public ResponseEntity<?> uploadAvatar(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("file") MultipartFile file
    ) {
        String avatarUrl = studentService.uploadAvatar(userDetails.getUsername(), file);

        APIResponse<AvatarUploadResponse> response = new APIResponse<>(
                200,
                "Upload avatar success!",
                new AvatarUploadResponse(avatarUrl)
        );

        return ResponseEntity.ok(response);
    }

    // [GET] /api/v2/student/program-progress
    @GetMapping("/program-progress")
    public ResponseEntity<?> programOfStudent(@AuthenticationPrincipal UserDetails userDetails) {
        ProgramResponse result = studentService.getProgramOfStudent(userDetails.getUsername());

        APIResponse response = new APIResponse(
                200,
                "Get program-progress success!",
                result
        );

        return ResponseEntity.ok(response);
    }

    // [GET] /api/v2/student/schedules
    @GetMapping("/schedules")
    public ResponseEntity<?> mySchedule(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long semesterId,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate
            ) {
        StudentTimetableResponse result = studentService.getMySchedule(userDetails.getUsername(), semesterId, fromDate, toDate);

        APIResponse response = new APIResponse(
                200,
                "Get my schedule success!",
                result
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/tuition")
    public ResponseEntity<?> myTuition(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long semesterId
    ) {
        List<StudentTuitionResponse> result = studentService.getMyTuition(userDetails.getUsername(), semesterId);

        APIResponse response = new APIResponse(
                200,
                "Get my tuition success!",
                result
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/payments")
    public ResponseEntity<?> myPaymentHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) PaymentStatus status
    ) {
        List<StudentPaymentHistoryResponse> result = studentService.getMyPaymentHistory(userDetails.getUsername(), status);

        APIResponse response = new APIResponse(
                200,
                "Get my payment history success!",
                result
        );

        return ResponseEntity.ok(response);
    }

}
