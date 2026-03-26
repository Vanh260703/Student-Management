package com.example.quan_ly_sinh_vien_v2.Service;

import com.example.quan_ly_sinh_vien_v2.Config.MomoProperties;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.MomoCallbackResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.MomoPaymentResponse;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Payment;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Semester;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.StudentProfile;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.TuitionFee;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.User;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.PaymentMethod;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.PaymentStatus;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.SemesterName;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.TuitionStatus;
import com.example.quan_ly_sinh_vien_v2.Repository.PaymentRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.StudentProfileRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.TuitionFeeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MomoServiceTest {
    @Mock
    private StudentProfileRepository studentProfileRepository;
    @Mock
    private TuitionFeeRepository tuitionFeeRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private HttpClient httpClient;

    private MomoProperties momoProperties;
    private MomoService momoService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        momoProperties = new MomoProperties();
        momoProperties.setPartnerCode("MOMOLRJZ20181206");
        momoProperties.setAccessKey("mTCKt9W3eU1m39TW");
        momoProperties.setSecretKey("SetA5RDnLHvt51AULf51DyauxUo3kDU6");
        momoProperties.setEndpoint("https://test-payment.momo.vn/v2/gateway/api");
        momoProperties.setRedirectUrl("http://localhost:8080/api/v2/payments/momo/return");
        momoProperties.setIpnUrl("http://localhost:8080/api/v2/payments/momo/ipn");
        momoProperties.setRequestType("captureWallet");
        momoProperties.setLang("vi");
        momoProperties.setPartnerName("MOMO");
        momoProperties.setStoreId("MOMO_STORE");

        momoService = new MomoService(
                momoProperties,
                studentProfileRepository,
                tuitionFeeRepository,
                paymentRepository,
                objectMapper,
                httpClient
        );
    }

    @Test
    void createPaymentUrlShouldCreatePendingPaymentAndReturnPayUrl() throws Exception {
        StudentProfile student = buildStudentProfile();
        TuitionFee tuitionFee = buildTuitionFee(student.getUser());

        when(studentProfileRepository.findStudentByEmail("student@example.com")).thenReturn(Optional.of(student));
        when(tuitionFeeRepository.findByIdAndStudentId(10L, student.getUser().getId())).thenReturn(Optional.of(tuitionFee));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.body()).thenReturn("""
                {
                  "resultCode":0,
                  "message":"Success",
                  "payUrl":"https://test-payment.momo.vn/pay/abc",
                  "deeplink":"momo://payment",
                  "qrCodeUrl":"https://test-payment.momo.vn/qr/abc"
                }
                """);
        when(httpClient.send(any(), any(HttpResponse.BodyHandler.class))).thenReturn(response);

        MomoPaymentResponse result = momoService.createPaymentUrl("student@example.com", 10L);

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentCaptor.capture());
        Payment savedPayment = paymentCaptor.getValue();

        assertEquals(PaymentMethod.MOMO, savedPayment.getMethod());
        assertEquals(PaymentStatus.PENDING, savedPayment.getStatus());
        assertEquals(1_600_000D, savedPayment.getAmount());
        assertEquals("https://test-payment.momo.vn/pay/abc", result.getPayUrl());
        assertEquals(PaymentStatus.PENDING, result.getStatus());
    }

    @Test
    void handleReturnShouldMarkPaymentSuccessWhenSignatureIsValid() {
        StudentProfile student = buildStudentProfile();
        TuitionFee tuitionFee = buildTuitionFee(student.getUser());
        Payment payment = new Payment();
        payment.setTuitionFee(tuitionFee);
        payment.setStudent(student.getUser());
        payment.setAmount(1_600_000D);
        payment.setMethod(PaymentMethod.MOMO);
        payment.setTransactionCode("MOMOORDER123");
        payment.setStatus(PaymentStatus.PENDING);

        when(paymentRepository.findByTransactionCode("MOMOORDER123")).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(tuitionFeeRepository.save(any(TuitionFee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("partnerCode", momoProperties.getPartnerCode());
        payload.put("orderId", "MOMOORDER123");
        payload.put("requestId", "REQ123");
        payload.put("amount", "1600000");
        payload.put("orderInfo", "Thanh toan hoc phi 20265098 HK1");
        payload.put("orderType", "momo_wallet");
        payload.put("transId", "123456789");
        payload.put("resultCode", "0");
        payload.put("message", "Successful.");
        payload.put("payType", "qr");
        payload.put("responseTime", "1710000000000");
        payload.put("extraData", "");
        payload.put("signature", signCallback(payload));

        MomoCallbackResponse result = momoService.handleReturn(payload);

        assertTrue(result.isSuccess());
        assertTrue(result.isSignatureValid());
        assertEquals(PaymentStatus.SUCCESS, payment.getStatus());
        assertEquals(TuitionStatus.PAID, tuitionFee.getStatus());
    }

    private StudentProfile buildStudentProfile() {
        User user = new User();
        user.setEmail("student@example.com");
        user.setFullName("Student Test");
        org.springframework.test.util.ReflectionTestUtils.setField(user, "id", 1L);

        StudentProfile student = new StudentProfile();
        student.setUser(user);
        student.setStudentCode("20265098");
        org.springframework.test.util.ReflectionTestUtils.setField(student, "id", 2L);
        return student;
    }

    private TuitionFee buildTuitionFee(User user) {
        Semester semester = new Semester();
        semester.setName(SemesterName.HK1);
        semester.setSemesterNumber(1);
        semester.setEndDate(LocalDate.of(2026, 6, 30));
        org.springframework.test.util.ReflectionTestUtils.setField(semester, "id", 3L);

        TuitionFee tuitionFee = new TuitionFee();
        tuitionFee.setStudent(user);
        tuitionFee.setSemester(semester);
        tuitionFee.setAmount(1_600_000D);
        tuitionFee.setDiscount(0D);
        tuitionFee.setFinalAmount(1_600_000D);
        tuitionFee.setDueDate(LocalDate.of(2026, 6, 30));
        tuitionFee.setStatus(TuitionStatus.PENDING);
        org.springframework.test.util.ReflectionTestUtils.setField(tuitionFee, "id", 10L);
        return tuitionFee;
    }

    private String signCallback(Map<String, String> payload) {
        String rawSignature = "accessKey=" + momoProperties.getAccessKey()
                + "&amount=" + payload.get("amount")
                + "&extraData=" + payload.get("extraData")
                + "&message=" + payload.get("message")
                + "&orderId=" + payload.get("orderId")
                + "&orderInfo=" + payload.get("orderInfo")
                + "&orderType=" + payload.get("orderType")
                + "&partnerCode=" + payload.get("partnerCode")
                + "&payType=" + payload.get("payType")
                + "&requestId=" + payload.get("requestId")
                + "&responseTime=" + payload.get("responseTime")
                + "&resultCode=" + payload.get("resultCode")
                + "&transId=" + payload.get("transId");

        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(momoProperties.getSecretKey().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmac.init(secretKeySpec);
            byte[] bytes = hmac.doFinal(rawSignature.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte currentByte : bytes) {
                builder.append(String.format("%02x", currentByte));
            }
            return builder.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
