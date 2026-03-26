package com.example.quan_ly_sinh_vien_v2.Controller;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.Semester.CreateSemesterRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Request.Semester.UpdateSemesterRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.APIResponse;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Semester;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.SemesterName;
import com.example.quan_ly_sinh_vien_v2.Service.SemesterService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/semesters")
public class SemesterController {
    private final SemesterService semesterService;

    public SemesterController(SemesterService semesterService) {
        this.semesterService = semesterService;
    }

    // [GET] /api/v2/semesters (ALL)
    @GetMapping()
    public ResponseEntity<?> semesters(
            @RequestParam(required = false) Long academicYearId,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Integer semesterNumber
            ) {
        List<Semester> semesters = semesterService.getSemesters(academicYearId, isActive, semesterNumber);

        APIResponse response = new APIResponse(
                200,
                "Get semesters success!",
                semesters
        );

        return ResponseEntity.ok(response);
    }

    // [POST] /api/v2/semesters (ADMIN ONLY)
    @PostMapping()
    public ResponseEntity<?> createSemester(@Valid @RequestBody CreateSemesterRequest request) {
        Semester semester = semesterService.createSemester(request);

        APIResponse response = new APIResponse(
                200,
                "Create semester success!",
                semester
        );

        return ResponseEntity.ok(response);
    }

    // [PUT] /api/v2/semesters/:semesterId (ADMIN ONLY)
    @PutMapping("/{semesterId}")
    public ResponseEntity<?> updateSemester(
            @PathVariable Long semesterId,
            @Valid @RequestBody UpdateSemesterRequest request
    ) {
        Semester semester = semesterService.updateSemester(semesterId, request);

        APIResponse response = new APIResponse(
                200,
                "Update semester success",
                semester
        );

        return ResponseEntity.ok(response);
    }

    // [PATCH] /api/v2/semesters/:semester/toggle-active
    @PatchMapping("/{semesterId}/toggle-active")
    public ResponseEntity<?> toggleActive(@PathVariable Long semesterId) {
        semesterService.toggleActive(semesterId);

        APIResponse response = new APIResponse(
                200,
                "Change semester status success",
                null
        );

        return ResponseEntity.ok(response);
    }
}
