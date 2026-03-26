package com.example.quan_ly_sinh_vien_v2.Controller;

import com.example.quan_ly_sinh_vien_v2.DTO.Response.APIResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.MomoCallbackResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.MomoPaymentResponse;
import com.example.quan_ly_sinh_vien_v2.Service.MomoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@RestController
@RequestMapping({"/api/v2/payments", "/api/payments"})
public class PaymentController {
    private final MomoService momoService;

    public PaymentController(MomoService momoService) {
        this.momoService = momoService;
    }

    @PostMapping("/momo/create/{tuitionId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<APIResponse<MomoPaymentResponse>> createMomoPaymentByTuitionId(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long tuitionId
    ) {
        MomoPaymentResponse result = momoService.createPaymentUrl(
                userDetails.getUsername(),
                tuitionId
        );

        APIResponse<MomoPaymentResponse> response = new APIResponse<>(
                200,
                "Create MoMo payment success!",
                result
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/momo/return")
    public ResponseEntity<APIResponse<MomoCallbackResponse>> handleReturn(
            @RequestParam Map<String, String> queryParams
    ) {
        MomoCallbackResponse result = momoService.handleReturn(queryParams);

        APIResponse<MomoCallbackResponse> response = new APIResponse<>(
                200,
                result.getMessage(),
                result
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/momo/ipn")
    public ResponseEntity<Void> handleIpn(
            @RequestBody Map<String, Object> payload
    ) {
        return ResponseEntity.status(momoService.handleIpn(payload)).build();
    }
}
