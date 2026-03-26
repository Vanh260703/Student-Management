package com.example.quan_ly_sinh_vien_v2.Controller;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.Subject.CreateSubjectRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Request.Subject.UpdateSubjectRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.APIResponse;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Subject;
import com.example.quan_ly_sinh_vien_v2.Service.SubjectService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/subjects")
public class SubjectController {
    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    // [GET] /api/v2/subjects (ALL)
    @GetMapping()
    public ResponseEntity<?> subjects(
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Integer credits
    ) {
        List<Subject> subjects = subjectService.getSubjects(departmentId, search, isActive, credits);

        APIResponse response = new APIResponse(
                200,
                "Get subjects success",
                subjects
        );

        return ResponseEntity.ok(response);
    }

    // [POST] /api/v2/subjects (ADMIN ONLY)
    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createSubject(@Valid @RequestBody CreateSubjectRequest request) {
        Subject subject = subjectService.createSubject(request);

        APIResponse response = new APIResponse(
                200,
                "Create subject success",
                subject
        );

        return ResponseEntity.ok(response);
    }

    // [GET] /api/v2/subjects/:subjectId (ALL)
    @GetMapping("/{subjectId}")
    public ResponseEntity<?> detailsSubject(@PathVariable Long subjectId) {
        Subject subject = subjectService.getDetailsSubject(subjectId);

        APIResponse response = new APIResponse(
                200,
                "Get details subject success",
                subject
        );

        return ResponseEntity.ok(response);
    }

    // [PUT] /api/v2/subjects/:subjectId
    @PutMapping("/{subjectId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateSubject(
            @PathVariable Long subjectId,
            @Valid @RequestBody UpdateSubjectRequest request
    ) {
        Subject subject = subjectService.updateSubject(subjectId, request);

        APIResponse response = new APIResponse(
                200,
                "Update subject success!",
                subject
        );

        return ResponseEntity.ok(response);
    }

    // [DELETE] /api/v2/subjects/:subjectId (ADMIN ONLY)
    @DeleteMapping("/{subjectId}")
    public ResponseEntity<?> deleteSubject(@PathVariable Long subjectId) {
        subjectService.deleteSubject(subjectId);

        APIResponse response = new APIResponse(
                200,
                "Delete subject success!",
                null
        );

        return ResponseEntity.ok(response);
    }
}
