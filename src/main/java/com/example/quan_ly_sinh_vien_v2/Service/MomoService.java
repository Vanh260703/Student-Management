package com.example.quan_ly_sinh_vien_v2.Service;

import com.example.quan_ly_sinh_vien_v2.Config.MomoProperties;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.MomoCallbackResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.MomoPaymentResponse;
import com.example.quan_ly_sinh_vien_v2.Exception.NotFoundException;
import com.example.quan_ly_sinh_vien_v2.Exception.UpdateFailException;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Payment;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.StudentProfile;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.TuitionFee;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.PaymentMethod;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.PaymentStatus;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.TuitionStatus;
import com.example.quan_ly_sinh_vien_v2.Repository.PaymentRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.StudentProfileRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.TuitionFeeRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class MomoService {
    private final MomoProperties momoProperties;
    private final StudentProfileRepository studentProfileRepository;
    private final TuitionFeeRepository tuitionFeeRepository;
    private final PaymentRepository paymentRepository;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    @Autowired
    public MomoService(
            MomoProperties momoProperties,
            StudentProfileRepository studentProfileRepository,
            TuitionFeeRepository tuitionFeeRepository,
            PaymentRepository paymentRepository
    ) {
        this.momoProperties = momoProperties;
        this.studentProfileRepository = studentProfileRepository;
        this.tuitionFeeRepository = tuitionFeeRepository;
        this.paymentRepository = paymentRepository;
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newHttpClient();
    }

    MomoService(
            MomoProperties momoProperties,
            StudentProfileRepository studentProfileRepository,
            TuitionFeeRepository tuitionFeeRepository,
            PaymentRepository paymentRepository,
            ObjectMapper objectMapper,
            HttpClient httpClient
    ) {
        this.momoProperties = momoProperties;
        this.studentProfileRepository = studentProfileRepository;
        this.tuitionFeeRepository = tuitionFeeRepository;
        this.paymentRepository = paymentRepository;
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
    }

    @Transactional
    public MomoPaymentResponse createPaymentUrl(String username, Long tuitionId) {
        validateConfiguration();

        StudentProfile student = studentProfileRepository.findStudentByEmail(username)
                .orElseThrow(() -> new NotFoundException("Student not found!"));

        TuitionFee tuitionFee = tuitionFeeRepository.findByIdAndStudentId(tuitionId, student.getUser().getId())
                .orElseThrow(() -> new NotFoundException("Tuition fee not found!"));

        if (tuitionFee.getStatus() == TuitionStatus.PAID) {
            throw new UpdateFailException("Tuition fee has already been paid");
        }

        double amount = resolveAmount(tuitionFee);
        if (amount < 1000D) {
            throw new UpdateFailException("MoMo requires amount to be at least 1000 VND");
        }

        Payment payment = new Payment();
        payment.setTuitionFee(tuitionFee);
        payment.setStudent(student.getUser());
        payment.setAmount(amount);
        payment.setMethod(PaymentMethod.MOMO);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setTransactionCode(generateOrderId());

        String requestId = generateRequestId();
        String orderInfo = buildOrderInfo(student, tuitionFee);
        String extraData = buildExtraData(tuitionFee.getId(), payment.getTransactionCode(), student.getStudentCode());

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("partnerCode", momoProperties.getPartnerCode());
        payload.put("partnerName", defaultString(momoProperties.getPartnerName()));
        payload.put("storeId", defaultString(momoProperties.getStoreId()));
        payload.put("requestId", requestId);
        payload.put("amount", Math.round(amount));
        payload.put("orderId", payment.getTransactionCode());
        payload.put("orderInfo", orderInfo);
        payload.put("redirectUrl", momoProperties.getRedirectUrl());
        payload.put("ipnUrl", momoProperties.getIpnUrl());
        payload.put("requestType", momoProperties.getRequestType());
        payload.put("extraData", extraData);
        payload.put("autoCapture", true);
        payload.put("lang", momoProperties.getLang());
        payload.put("signature", signCreateRequest(
                Math.round(amount),
                payment.getTransactionCode(),
                orderInfo,
                requestId,
                extraData
        ));

        Map<String, Object> response = callCreateOrder(payload);
        Integer resultCode = asInteger(response.get("resultCode"));
        if (resultCode == null || resultCode != 0) {
            throw new UpdateFailException("MoMo create payment failed: " + response.getOrDefault("message", "Unknown error"));
        }

        payment.setGatewayResponse(serialize(response));
        paymentRepository.save(payment);

        return new MomoPaymentResponse(
                payment.getId(),
                payment.getTransactionCode(),
                requestId,
                payment.getAmount(),
                payment.getStatus(),
                asString(response.get("payUrl")),
                asString(response.get("deeplink")),
                asString(response.get("qrCodeUrl"))
        );
    }

    @Transactional
    public MomoCallbackResponse handleReturn(Map<String, String> queryParams) {
        return processCallback(queryParams, false);
    }

    @Transactional
    public HttpStatus handleIpn(Map<String, Object> payload) {
        Map<String, String> normalized = normalizePayload(payload);
        processCallback(normalized, true);
        return HttpStatus.NO_CONTENT;
    }

    private MomoCallbackResponse processCallback(Map<String, String> payload, boolean ipnRequest) {
        String orderId = payload.get("orderId");
        if (!hasText(orderId)) {
            throw new UpdateFailException("Missing orderId");
        }

        Payment payment = paymentRepository.findByTransactionCode(orderId)
                .orElseThrow(() -> new NotFoundException("Payment not found!"));

        boolean signatureValid = isValidCallbackSignature(payload);
        Integer resultCode = asInteger(payload.get("resultCode"));
        boolean success = signatureValid && Objects.equals(resultCode, 0);

        payment.setGatewayResponse(serialize(payload));

        if (signatureValid) {
            payment.setStatus(success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED);
            if (success) {
                payment.setPaidAt(LocalDateTime.now());
            }
        } else if (!ipnRequest) {
            payment.setStatus(PaymentStatus.FAILED);
        }

        paymentRepository.save(payment);

        TuitionFee tuitionFee = payment.getTuitionFee();
        if (signatureValid) {
            tuitionFee.setStatus(success ? TuitionStatus.PAID : TuitionStatus.PENDING);
            tuitionFeeRepository.save(tuitionFee);
        }

        return new MomoCallbackResponse(
                orderId,
                payload.get("requestId"),
                asLong(payload.get("transId")),
                payment.getStatus(),
                resultCode,
                payload.get("message"),
                signatureValid,
                success
        );
    }

    private Map<String, Object> callCreateOrder(Map<String, Object> payload) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(resolveCreateEndpoint()))
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(response.body(), new TypeReference<>() {});
        } catch (Exception ex) {
            throw new UpdateFailException("Unable to connect to MoMo");
        }
    }

    private String resolveCreateEndpoint() {
        String endpoint = momoProperties.getEndpoint();
        if (!hasText(endpoint)) {
            throw new UpdateFailException("MoMo endpoint is not configured");
        }

        if (endpoint.endsWith("/create")) {
            return endpoint;
        }

        if (endpoint.endsWith("/")) {
            return endpoint + "create";
        }

        return endpoint + "/create";
    }

    private String signCreateRequest(
            long amount,
            String orderId,
            String orderInfo,
            String requestId,
            String extraData
    ) {
        String rawSignature = "accessKey=" + momoProperties.getAccessKey()
                + "&amount=" + amount
                + "&extraData=" + extraData
                + "&ipnUrl=" + momoProperties.getIpnUrl()
                + "&orderId=" + orderId
                + "&orderInfo=" + orderInfo
                + "&partnerCode=" + momoProperties.getPartnerCode()
                + "&redirectUrl=" + momoProperties.getRedirectUrl()
                + "&requestId=" + requestId
                + "&requestType=" + momoProperties.getRequestType();

        return hmacSha256(rawSignature, momoProperties.getSecretKey());
    }

    private boolean isValidCallbackSignature(Map<String, String> payload) {
        String providedSignature = payload.get("signature");
        if (!hasText(providedSignature)) {
            return false;
        }

        String rawSignature = "accessKey=" + momoProperties.getAccessKey()
                + "&amount=" + defaultString(payload.get("amount"))
                + "&extraData=" + defaultString(payload.get("extraData"))
                + "&message=" + defaultString(payload.get("message"))
                + "&orderId=" + defaultString(payload.get("orderId"))
                + "&orderInfo=" + defaultString(payload.get("orderInfo"))
                + "&orderType=" + defaultString(payload.get("orderType"))
                + "&partnerCode=" + defaultString(payload.get("partnerCode"))
                + "&payType=" + defaultString(payload.get("payType"))
                + "&requestId=" + defaultString(payload.get("requestId"))
                + "&responseTime=" + defaultString(payload.get("responseTime"))
                + "&resultCode=" + defaultString(payload.get("resultCode"))
                + "&transId=" + defaultString(payload.get("transId"));

        String expectedSignature = hmacSha256(rawSignature, momoProperties.getSecretKey());
        return MessageDigest.isEqual(
                expectedSignature.getBytes(StandardCharsets.UTF_8),
                providedSignature.getBytes(StandardCharsets.UTF_8)
        );
    }

    private String hmacSha256(String data, String secretKey) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmac.init(secretKeySpec);
            byte[] bytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder builder = new StringBuilder(bytes.length * 2);
            for (byte currentByte : bytes) {
                builder.append(String.format("%02x", currentByte));
            }

            return builder.toString();
        } catch (Exception ex) {
            throw new UpdateFailException("Unable to sign MoMo request");
        }
    }

    private void validateConfiguration() {
        if (!hasText(momoProperties.getPartnerCode())
                || !hasText(momoProperties.getAccessKey())
                || !hasText(momoProperties.getSecretKey())
                || !hasText(momoProperties.getEndpoint())
                || !hasText(momoProperties.getRedirectUrl())
                || !hasText(momoProperties.getIpnUrl())
                || !hasText(momoProperties.getRequestType())) {
            throw new UpdateFailException("MoMo configuration is incomplete");
        }
    }

    private String buildOrderInfo(StudentProfile student, TuitionFee tuitionFee) {
        String semesterName = tuitionFee.getSemester() != null && tuitionFee.getSemester().getName() != null
                ? tuitionFee.getSemester().getName().name()
                : "UNKNOWN";

        return "Thanh toan hoc phi " + student.getStudentCode() + " " + semesterName;
    }

    private String buildExtraData(Long tuitionId, String orderId, String studentCode) {
        try {
            String json = objectMapper.writeValueAsString(Map.of(
                    "tuitionId", tuitionId,
                    "orderId", orderId,
                    "studentCode", studentCode
            ));
            return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (Exception ex) {
            throw new UpdateFailException("Unable to build MoMo extraData");
        }
    }

    private String generateOrderId() {
        return "MOMO" + UUID.randomUUID().toString().replace("-", "").substring(0, 24).toUpperCase();
    }

    private String generateRequestId() {
        return "REQ" + UUID.randomUUID().toString().replace("-", "").substring(0, 24).toUpperCase();
    }

    private double resolveAmount(TuitionFee tuitionFee) {
        if (tuitionFee.getFinalAmount() != null) {
            return tuitionFee.getFinalAmount();
        }

        double baseAmount = tuitionFee.getAmount() != null ? tuitionFee.getAmount() : 0D;
        double discount = tuitionFee.getDiscount() != null ? tuitionFee.getDiscount() : 0D;
        return Math.max(baseAmount - discount, 0D);
    }

    private Map<String, String> normalizePayload(Map<String, Object> payload) {
        Map<String, String> result = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : payload.entrySet()) {
            result.put(entry.getKey(), entry.getValue() == null ? "" : String.valueOf(entry.getValue()));
        }
        return result;
    }

    private String serialize(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception ex) {
            return String.valueOf(value);
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }

    private String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private Integer asInteger(Object value) {
        if (value == null) {
            return null;
        }
        return Integer.parseInt(String.valueOf(value));
    }

    private Long asLong(Object value) {
        if (value == null || String.valueOf(value).isBlank()) {
            return null;
        }
        return Long.parseLong(String.valueOf(value));
    }
}
