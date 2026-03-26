package com.example.quan_ly_sinh_vien_v2.Service;

import com.example.quan_ly_sinh_vien_v2.DTO.Response.ClassResponse;
import com.example.quan_ly_sinh_vien_v2.Exception.CreateFailException;
import com.example.quan_ly_sinh_vien_v2.Exception.NotFoundException;
import com.example.quan_ly_sinh_vien_v2.Helper;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.*;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.ClassStatus;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.EnrollmentStatus;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.LetterGrade;
import com.example.quan_ly_sinh_vien_v2.Repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final ClassEntityRepository classEntityRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final GradeRepository gradeRepository;
    private final GradeComponentRepository gradeComponentRepository;
    public EnrollmentService(EnrollmentRepository enrollmentRepository, ClassEntityRepository classEntityRepository, StudentProfileRepository studentProfileRepository, GradeRepository gradeRepository, GradeComponentRepository gradeComponentRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.classEntityRepository = classEntityRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.gradeRepository = gradeRepository;
        this.gradeComponentRepository = gradeComponentRepository;
    }

    // Get available classes
    public List<ClassResponse> getAvailableClasses(String username, Long semesterId, Long subjectId, Long departmentId, Boolean hasSlot, Boolean notEnrolled, String search) {
        StudentProfile student = studentProfileRepository.findStudentByEmail(username)
                .orElseThrow(() -> new NotFoundException("Student not found!"));

        if (hasSlot == null) {
            hasSlot = true;
        }

        if (notEnrolled == null) {
            notEnrolled = true;
        }

        String keyword = null;

        if (search != null && !search.trim().isEmpty()) {
            keyword = search.trim().toLowerCase();
        }

        List<ClassEntity> classes = classEntityRepository.findAvailableClasses(
                semesterId,
                subjectId,
                departmentId,
                keyword,
                hasSlot,
                notEnrolled,
                student.getId()
                );

        return classes.stream().map(ClassResponse::from).toList();
    }

    // Register class
    public void enrollInClass(String username, Long classId) {
        ClassEntity classEntity = classEntityRepository.findById(classId)
                .orElseThrow(() -> new NotFoundException("Class not found!"));

        StudentProfile student = studentProfileRepository.findStudentByEmail(username)
                .orElseThrow(() -> new NotFoundException("Student not found!"));

        if (classEntity.getStatus() != ClassStatus.OPEN) {
            throw new CreateFailException("Class is invalid!");
        }

        if (classEntity.getCurrentStudents() >= classEntity.getMaxStudents()) {
            throw new CreateFailException("Class is invalid!");
        }

        Enrollment enrollment = new Enrollment();

        enrollment.setStudent(student);
        enrollment.setClassEntity(classEntity);
        enrollment.setEnrolledAt(LocalDateTime.now());
        enrollment.setStatus(EnrollmentStatus.ENROLLED);

        enrollmentRepository.save(enrollment);
    }

    // Cancel register class
    public void deleteEnrollment(Long enrollmentId) {
        if (!enrollmentRepository.existsById(enrollmentId)) {
            throw new NotFoundException("Enrollment not found!");
        }

        enrollmentRepository.deleteById(enrollmentId);
    }

    // Get my enrollments
    public List<Enrollment> myEnrollments(String username, Long semesterId, EnrollmentStatus status) {
        StudentProfile student = studentProfileRepository.findStudentByEmail(username)
                .orElseThrow(() -> new NotFoundException("Student not found"));

        Long studentId = student.getId();

        return enrollmentRepository.findMyClasses(studentId, semesterId, status);
    }

    // Get details enrollment
    public Enrollment getDetailsEnrollment(Long enrollmentId) {
        return enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new NotFoundException("Enrollment not found!"));
    }

    public void recalculateFinalScore(Enrollment enrollment) {
        List<Grade> grades = gradeRepository.findAllByEnrollment(enrollment);
        List<GradeComponent> gradeComponents = gradeComponentRepository.findAllByClassEntityId(enrollment.getClassEntity().getId());

        Double total = 0D;

        boolean hasAllGradeComponent = gradeComponents.stream().allMatch(gradeComponent ->
                grades.stream().anyMatch(g ->
                        g.getGradeComponent().getId().equals(gradeComponent.getId())
                                && g.getScore() != null
                )
        );

        if (!hasAllGradeComponent) {
            enrollment.setFinalScore(null);
            enrollment.setFinalLetterGrade(null);

            return;
        }

        for (Grade grade : grades) {
            Integer weight = grade.getGradeComponent().getWeight();

            double score = (grade.getScore() * weight) / 100;

            total += score;
        }

        LetterGrade finalLetterGrade = Helper.convertScoreToLetterGrade(total);

        boolean isPassed = false;

        if (total >= 4) {
            isPassed = true;
        }

        enrollment.setFinalScore(total);
        enrollment.setFinalLetterGrade(finalLetterGrade);
        enrollment.setIsPassed(isPassed);
    }
}
