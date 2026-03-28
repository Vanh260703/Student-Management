package com.example.quan_ly_sinh_vien_v2.Controller.Admin;

import com.example.quan_ly_sinh_vien_v2.DTO.Response.APIResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.Admin.AdminPaymentResponse;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.PaymentStatus;
import com.example.quan_ly_sinh_vien_v2.Service.Admin.AdminPaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v2/admin/payments")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPaymentController {
    private final AdminPaymentService adminPaymentService;

    public AdminPaymentController(AdminPaymentService adminPaymentService) {
        this.adminPaymentService = adminPaymentService;
    }

    @GetMapping
    public ResponseEntity<?> getAllPayments(
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(required = false) Long semesterId,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<AdminPaymentResponse> result = adminPaymentService.getAllPayments(
                status,
                semesterId,
                studentId,
                fromDate,
                endDate
        );

        APIResponse response = new APIResponse(
                200,
                "Get all payments success!",
                result
        );

        return ResponseEntity.ok(response);
    }
}
