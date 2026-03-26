package com.example.quan_ly_sinh_vien_v2.DTO.Response;

import com.example.quan_ly_sinh_vien_v2.Modal.Enum.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MomoCallbackResponse {
    private String orderId;
    private String requestId;
    private Long transId;
    private PaymentStatus paymentStatus;
    private Integer resultCode;
    private String message;
    private boolean signatureValid;
    private boolean success;
}
