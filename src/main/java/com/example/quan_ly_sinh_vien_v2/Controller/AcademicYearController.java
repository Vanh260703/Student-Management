package com.example.quan_ly_sinh_vien_v2.Controller;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.AcademicYear.CreateAcademicYearRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.APIResponse;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.AcademicYear;
import com.example.quan_ly_sinh_vien_v2.Service.AcademicYearService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/academic-years")
public class AcademicYearController {
    private final AcademicYearService academicYearService;

    public AcademicYearController(AcademicYearService academicYearService) {
        this.academicYearService = academicYearService;
    }

    // [GET] /api/v2/academic-years (ALL)
    @GetMapping()
    public ResponseEntity<?> academicYears(@RequestParam(required = false) Boolean isCurrent) {
        List<AcademicYear> academicYears = academicYearService.getAcademicYears(isCurrent);

        APIResponse response = new APIResponse(
                200,
                "Get academic years success",
                academicYears
        );

        return ResponseEntity.ok(response);
    }

    // [POST] /api/v2/academic-years (ADMIN ONLY)
    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createAcademicYear(@Valid @RequestBody CreateAcademicYearRequest request) {
        AcademicYear academicYear = academicYearService.createAcademicYear(request);

        APIResponse response = new APIResponse(
                200,
                "Create academic year success",
                academicYear
        );

        return ResponseEntity.ok(response);
    }
}
