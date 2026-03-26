package com.example.quan_ly_sinh_vien_v2.Controller;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.Program.CreateProgramRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Request.Program.CreateProgramSubjectRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Request.Program.UpdateProgramRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.APIResponse;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Program;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.ProgramSubject;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Subject;
import com.example.quan_ly_sinh_vien_v2.Service.ProgramService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/api/v2/programs")
public class ProgramController {
    private final ProgramService programService;

    public ProgramController(ProgramService programService) {
        this.programService = programService;
    }

    // [GET] /api/v2/programs (ALL)
    @GetMapping()
    public ResponseEntity<?> programs(
           @RequestParam(required = false) Long departmentId,
           @RequestParam(required = false) String search
    ) {
        List<Program> programs = programService.getPrograms(departmentId, search);

        APIResponse response = new APIResponse(
                200,
                "Get programs success",
                programs
        );

        return ResponseEntity.ok(response);
    }

    // [POST] /api/v2/programs (ADMIN ONLY)
    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createProgram(@Valid @RequestBody CreateProgramRequest request) {
        Program program = programService.createProgram(request);

        APIResponse response = new APIResponse(
                200,
                "Create program success!",
                program
        );

        return ResponseEntity.ok(response);
    }

    // [GET] /api/v2/programs/:programId (ALL)
    @GetMapping("/{programId}")
    public ResponseEntity<?> getProgram(@PathVariable Long programId) {
        Program program = programService.getDetailsProgram(programId);

        APIResponse response = new APIResponse(
                200,
                "Get details program success!",
                program
        );

        return ResponseEntity.ok(response);
    }

    // [PUT] /api/v2/programs/:programId (ADMIN ONLY)
    @PutMapping("/{programId}")
    public ResponseEntity<?> updateProgram(
            @PathVariable Long programId,
            @Valid @RequestBody UpdateProgramRequest request) {
        Program program = programService.updateProgram(programId, request);

        APIResponse response = new APIResponse(
                200,
                "Update program success!",
                program
        );

        return ResponseEntity.ok(response);
    }

    // [DELETE] /api/v2/programs/:programId (ADMIN ONLY)
    @DeleteMapping("/{programId}")
    public ResponseEntity<?> deleteProgram(@PathVariable Long programId) {
       programService.deleteProgram(programId);

        APIResponse response = new APIResponse(
                200,
                "Delete program success!",
                null
        );

        return ResponseEntity.ok(response);
    }

    // [GET] /api/v2/programs/:programId/subjects (ALL)
    @GetMapping("/{programId}/subjects")
    public ResponseEntity<?> subjectsInProgram(
            @PathVariable Long programId,
            @RequestParam(required = false) Integer semester,
            @RequestParam(required = false) Boolean isRequired
    ) {
        List<ProgramSubject> subjects = programService.getSubjectsInProgram(programId, semester, isRequired);

        APIResponse response = new APIResponse(
                200,
                "Get subjects in program success!",
                subjects
        );

        return ResponseEntity.ok(response);
    }

    // [POST] /api/v2/programs/:programId/subjects (ADMIN ONLY)
    @PostMapping("/{programId}/subjects")
    public ResponseEntity<?> addSubjectInProgram(
            @PathVariable Long programId,
            @Valid @RequestBody CreateProgramSubjectRequest request
    ) {
        ProgramSubject subject = programService.addSubjectInProgram(programId, request);

        APIResponse response = new APIResponse(
                200,
                "Add subject in program success!",
                subject
        );

        return ResponseEntity.ok(response);
    }

    // [DELETE] /api/v2/programs/:programId/subjects/:subjectId (ADMIN ONLY)
    @DeleteMapping("/{programId}/subjects/{subjectId}")
    public ResponseEntity<?> deleteSubjectInProgram(
            @PathVariable Long programId,
            @PathVariable Long subjectId
    ) {
        programService.deleteSubjectInProgram(programId, subjectId);

        APIResponse response = new APIResponse(
                200,
                "Remove subject from program success!",
                null
        );

        return ResponseEntity.ok(response);
    }
}
