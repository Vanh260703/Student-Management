package com.example.quan_ly_sinh_vien_v2.Service;

import com.example.quan_ly_sinh_vien_v2.Modal.Entity.ClassEntity;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Enrollment;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Grade;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.GradeComponent;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.StudentProfile;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.User;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.ClassStatus;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.EnrollmentStatus;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.GradeComponentType;
import com.example.quan_ly_sinh_vien_v2.Repository.ClassEntityRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.EnrollmentRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.GradeComponentRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.GradeRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.StudentProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {
    @Mock private EnrollmentRepository enrollmentRepository;
    @Mock private ClassEntityRepository classEntityRepository;
    @Mock private StudentProfileRepository studentProfileRepository;
    @Mock private GradeRepository gradeRepository;
    @Mock private GradeComponentRepository gradeComponentRepository;

    private EnrollmentService enrollmentService;

    @BeforeEach
    void setUp() {
        enrollmentService = new EnrollmentService(
                enrollmentRepository,
                classEntityRepository,
                studentProfileRepository,
                gradeRepository,
                gradeComponentRepository
        );
    }

    @Test
    void enrollInClassShouldPersistEnrollmentWhenClassOpenAndHasSlot() {
        ClassEntity classEntity = new ClassEntity();
        classEntity.setStatus(ClassStatus.OPEN);
        classEntity.setCurrentStudents(10);
        classEntity.setMaxStudents(20);

        StudentProfile student = new StudentProfile();
        ReflectionTestUtils.setField(student, "id", 2L);

        when(classEntityRepository.findById(100L)).thenReturn(Optional.of(classEntity));
        when(studentProfileRepository.findStudentByEmail("student@example.com")).thenReturn(Optional.of(student));
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        enrollmentService.enrollInClass("student@example.com", 100L);

        verify(enrollmentRepository).save(any(Enrollment.class));
    }

    @Test
    void recalculateFinalScoreShouldSetScoreWhenAllComponentsPresent() {
        Enrollment enrollment = new Enrollment();
        ClassEntity classEntity = new ClassEntity();
        ReflectionTestUtils.setField(classEntity, "id", 99L);
        enrollment.setClassEntity(classEntity);

        GradeComponent midterm = new GradeComponent();
        ReflectionTestUtils.setField(midterm, "id", 1L);
        midterm.setWeight(40);
        midterm.setType(GradeComponentType.MIDTERM);

        GradeComponent finalExam = new GradeComponent();
        ReflectionTestUtils.setField(finalExam, "id", 2L);
        finalExam.setWeight(60);
        finalExam.setType(GradeComponentType.FINAL);

        Grade gradeOne = new Grade();
        gradeOne.setGradeComponent(midterm);
        gradeOne.setScore(8.0);

        Grade gradeTwo = new Grade();
        gradeTwo.setGradeComponent(finalExam);
        gradeTwo.setScore(7.0);

        when(gradeRepository.findAllByEnrollment(enrollment)).thenReturn(List.of(gradeOne, gradeTwo));
        when(gradeComponentRepository.findAllByClassEntityId(99L)).thenReturn(List.of(midterm, finalExam));

        enrollmentService.recalculateFinalScore(enrollment);

        assertEquals(7.4, enrollment.getFinalScore());
    }
}
