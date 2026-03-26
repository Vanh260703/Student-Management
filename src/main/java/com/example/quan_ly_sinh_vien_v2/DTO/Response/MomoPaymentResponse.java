package com.example.quan_ly_sinh_vien_v2.DTO.Response;

import com.example.quan_ly_sinh_vien_v2.Modal.Enum.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MomoPaymentResponse {
    private Long paymentId;
    private String orderId;
    private String requestId;
    private Double amount;
    private PaymentStatus status;
    private String payUrl;
    private String deeplink;
    private String qrCodeUrl;
}
