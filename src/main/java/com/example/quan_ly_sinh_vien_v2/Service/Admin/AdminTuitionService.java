package com.example.quan_ly_sinh_vien_v2.Service.Admin;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.Admin.UpdateTuitionRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.Admin.GenerateTuitionResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.Admin.TuitionResponse;
import com.example.quan_ly_sinh_vien_v2.Exception.NotFoundException;
import com.example.quan_ly_sinh_vien_v2.Exception.UpdateFailException;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Enrollment;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Semester;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.StudentProfile;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.TuitionFee;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.EnrollmentStatus;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.TuitionStatus;
import com.example.quan_ly_sinh_vien_v2.Repository.EnrollmentRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.SemesterRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.StudentProfileRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.TuitionFeeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminTuitionService {
    private static final int CREDIT_PRICE = 400_000;

    private final SemesterRepository semesterRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final TuitionFeeRepository tuitionFeeRepository;
    private final StudentProfileRepository studentProfileRepository;

    public AdminTuitionService(
            SemesterRepository semesterRepository,
            EnrollmentRepository enrollmentRepository,
            TuitionFeeRepository tuitionFeeRepository,
            StudentProfileRepository studentProfileRepository
    ) {
        this.semesterRepository = semesterRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.tuitionFeeRepository = tuitionFeeRepository;
        this.studentProfileRepository = studentProfileRepository;
    }

    @Transactional
    public GenerateTuitionResponse generateCurrentSemesterTuition() {
        LocalDate today = LocalDate.now();
        Instant now = Instant.now();

        Semester currentSemester = semesterRepository
                .findFirstByIsActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(today, today)
                .orElseThrow(() -> new UpdateFailException("Current semester not found!"));

        if (currentSemester.getRegistrationEnd() == null) {
            throw new UpdateFailException("Current semester registration end is not configured!");
        }

        if (now.isBefore(currentSemester.getRegistrationEnd())) {
            throw new UpdateFailException("Cannot generate tuition before registration end!");
        }

        List<Enrollment> enrollments = enrollmentRepository.findBySemesterIdAndStatus(
                currentSemester.getId(),
                EnrollmentStatus.ENROLLED
        );

        if (enrollments.isEmpty()) {
            throw new UpdateFailException("No enrolled enrollments found in current semester!");
        }

        Map<Long, Double> tuitionByStudent = new LinkedHashMap<>();
        Map<Long, Enrollment> representativeEnrollmentByStudent = new LinkedHashMap<>();

        for (Enrollment enrollment : enrollments) {
            Integer credits = enrollment.getClassEntity().getSubject().getCredits();
            if (credits == null || credits <= 0) {
                continue;
            }

            Long studentId = enrollment.getStudent().getUser().getId();
            double subjectFee = (double) credits * CREDIT_PRICE;

            tuitionByStudent.merge(studentId, subjectFee, Double::sum);
            representativeEnrollmentByStudent.putIfAbsent(studentId, enrollment);
        }

        if (tuitionByStudent.isEmpty()) {
            throw new UpdateFailException("No valid enrolled credits found to generate tuition!");
        }

        int generatedCount = 0;
        int skippedCount = 0;

        for (Map.Entry<Long, Double> entry : tuitionByStudent.entrySet()) {
            Long studentId = entry.getKey();

            if (tuitionFeeRepository.existsByStudentIdAndSemesterId(studentId, currentSemester.getId())) {
                skippedCount++;
                continue;
            }

            Enrollment enrollment = representativeEnrollmentByStudent.get(studentId);

            TuitionFee tuitionFee = new TuitionFee();
            tuitionFee.setStudent(enrollment.getStudent().getUser());
            tuitionFee.setSemester(currentSemester);
            tuitionFee.setAmount(entry.getValue());
            tuitionFee.setDiscount(0D);
            tuitionFee.setFinalAmount(entry.getValue());
            tuitionFee.setDueDate(currentSemester.getEndDate());
            tuitionFee.setStatus(TuitionStatus.PENDING);

            tuitionFeeRepository.save(tuitionFee);
            generatedCount++;
        }

        GenerateTuitionResponse response = new GenerateTuitionResponse();
        response.setSemesterId(currentSemester.getId());
        response.setSemesterName(currentSemester.getName().name());
        response.setCreditPrice(CREDIT_PRICE);
        response.setTotalEnrollments(enrollments.size());
        response.setTotalStudents(tuitionByStudent.size());
        response.setGeneratedCount(generatedCount);
        response.setSkippedCount(skippedCount);

        return response;
    }

    public List<TuitionResponse> getAllTuition() {
        List<TuitionFee> tuitionFees = tuitionFeeRepository.findAllByOrderByCreatedAtDesc();
        List<TuitionResponse> responses = new ArrayList<>();

        for (TuitionFee tuitionFee : tuitionFees) {
            StudentProfile studentProfile = studentProfileRepository.findByUserId(tuitionFee.getStudent().getId())
                    .orElse(null);
            responses.add(TuitionResponse.from(tuitionFee, studentProfile));
        }

        return responses;
    }

    @Transactional
    public TuitionResponse updateTuition(Long tuitionId, UpdateTuitionRequest request) {
        TuitionFee tuitionFee = tuitionFeeRepository.findById(tuitionId)
                .orElseThrow(() -> new NotFoundException("Tuition not found!"));

        if (request.getAmount() == null
                && request.getDiscount() == null
                && request.getDueDate() == null
                && request.getStatus() == null) {
            throw new UpdateFailException("No data provided to update tuition!");
        }

        if (tuitionFee.getStatus() == TuitionStatus.PAID
                && (request.getAmount() != null || request.getDiscount() != null || request.getDueDate() != null)) {
            throw new UpdateFailException("Cannot change amount, discount or due date of a paid tuition!");
        }

        if (request.getAmount() != null) {
            if (request.getAmount() < 0) {
                throw new UpdateFailException("Amount must be greater than or equal to 0!");
            }
            tuitionFee.setAmount(request.getAmount());
        }

        if (request.getDiscount() != null) {
            if (request.getDiscount() < 0) {
                throw new UpdateFailException("Discount must be greater than or equal to 0!");
            }
            tuitionFee.setDiscount(request.getDiscount());
        }

        if (request.getDueDate() != null) {
            tuitionFee.setDueDate(request.getDueDate());
        }

        if (request.getStatus() != null) {
            if (request.getStatus() == TuitionStatus.PAID && tuitionFee.getStatus() != TuitionStatus.PAID) {
                throw new UpdateFailException("Paid status should only be updated by payment flow!");
            }
            tuitionFee.setStatus(request.getStatus());
        }

        double amount = tuitionFee.getAmount() != null ? tuitionFee.getAmount() : 0D;
        double discount = tuitionFee.getDiscount() != null ? tuitionFee.getDiscount() : 0D;
        tuitionFee.setFinalAmount(Math.max(amount - discount, 0D));

        StudentProfile studentProfile = studentProfileRepository.findByUserId(tuitionFee.getStudent().getId())
                .orElse(null);

        return TuitionResponse.from(tuitionFee, studentProfile);
    }
}
