package com.example.quan_ly_sinh_vien_v2.Controller.Admin;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.Admin.UpdateTuitionRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.APIResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.Admin.GenerateTuitionResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.Admin.TuitionResponse;
import com.example.quan_ly_sinh_vien_v2.Service.Admin.AdminTuitionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/admin/tuition")
@PreAuthorize("hasRole('ADMIN')")
public class AdminTuitionController {
    private final AdminTuitionService adminTuitionService;

    public AdminTuitionController(AdminTuitionService adminTuitionService) {
        this.adminTuitionService = adminTuitionService;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateTuition() {
        GenerateTuitionResponse result = adminTuitionService.generateCurrentSemesterTuition();

        APIResponse response = new APIResponse(
                200,
                "Generate tuition success!",
                result
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping()
    public ResponseEntity<?> getAllTuition() {
        List<TuitionResponse> result = adminTuitionService.getAllTuition();

        APIResponse response = new APIResponse(
                200,
                "Get all tuition success!",
                result
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{tuitionId}")
    public ResponseEntity<?> updateTuition(
            @PathVariable Long tuitionId,
            @RequestBody UpdateTuitionRequest request
    ) {
        TuitionResponse result = adminTuitionService.updateTuition(tuitionId, request);

        APIResponse response = new APIResponse(
                200,
                "Update tuition success!",
                result
        );

        return ResponseEntity.ok(response);
    }
}
