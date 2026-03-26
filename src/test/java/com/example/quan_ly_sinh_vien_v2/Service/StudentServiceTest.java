package com.example.quan_ly_sinh_vien_v2.Service;

import com.example.quan_ly_sinh_vien_v2.Config.MinioProperties;
import com.example.quan_ly_sinh_vien_v2.DTO.Request.Student.UpdateProfileRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.StudentPaymentHistoryResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.StudentProfileResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.StudentTuitionResponse;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Payment;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.AcademicYear;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Department;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Program;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Semester;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.StudentProfile;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.TuitionFee;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.User;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.EnrollmentStatus;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.PaymentMethod;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.PaymentStatus;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.SemesterName;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.TuitionStatus;
import com.example.quan_ly_sinh_vien_v2.Exception.UpdateFailException;
import com.example.quan_ly_sinh_vien_v2.Repository.ClassEntityRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.ClassScheduleRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.EnrollmentRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.GradeRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.PaymentRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.ProgramRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.ProgramSubjectRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.SemesterRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.StudentProfileRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.SubjectRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.TuitionFeeRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.UserRepository;
import io.minio.MinioClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {
    @Mock private StudentProfileRepository studentProfileRepository;
    @Mock private SemesterRepository semesterRepository;
    @Mock private ClassEntityRepository classEntityRepository;
    @Mock private GradeRepository gradeRepository;
    @Mock private ProgramRepository programRepository;
    @Mock private ProgramSubjectRepository programSubjectRepository;
    @Mock private SubjectRepository subjectRepository;
    @Mock private EnrollmentRepository enrollmentRepository;
    @Mock private TuitionFeeRepository tuitionFeeRepository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private UserRepository userRepository;
    @Mock private ClassScheduleRepository classScheduleRepository;
    @Mock private MinioClient minioClient;

    private StudentService studentService;

    @BeforeEach
    void setUp() {
        studentService = new StudentService(
                studentProfileRepository,
                semesterRepository,
                classEntityRepository,
                gradeRepository,
                programRepository,
                programSubjectRepository,
                subjectRepository,
                enrollmentRepository,
                tuitionFeeRepository,
                paymentRepository,
                userRepository,
                classScheduleRepository,
                minioClient,
                new MinioProperties()
        );
    }

    @Test
    void getMyTuitionShouldMapTuitionBySemester() {
        StudentProfile student = buildStudent();
        TuitionFee tuitionFee = buildTuition(student.getUser(), 11L, 7L);

        when(studentProfileRepository.findStudentByEmail("student@example.com")).thenReturn(Optional.of(student));
        when(tuitionFeeRepository.findByStudentIdAndSemesterIdOrderByCreatedAtDesc(1L, 7L))
                .thenReturn(List.of(tuitionFee));

        List<StudentTuitionResponse> result = studentService.getMyTuition("student@example.com", 7L);

        assertEquals(1, result.size());
        assertEquals(11L, result.get(0).getId());
        assertEquals(7L, result.get(0).getSemester().getId());
    }

    @Test
    void getMyPaymentHistoryShouldReturnOnlyRequestedStatus() {
        StudentProfile student = buildStudent();
        TuitionFee tuitionFee = buildTuition(student.getUser(), 11L, 7L);
        Payment payment = new Payment();
        payment.setTuitionFee(tuitionFee);
        payment.setStudent(student.getUser());
        payment.setAmount(1_600_000D);
        payment.setMethod(PaymentMethod.MOMO);
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaidAt(LocalDateTime.of(2026, 3, 26, 14, 0));
        payment.setTransactionCode("MOMO123");
        org.springframework.test.util.ReflectionTestUtils.setField(payment, "id", 21L);
        org.springframework.test.util.ReflectionTestUtils.setField(payment, "createdAt", LocalDateTime.of(2026, 3, 26, 13, 55));

        when(studentProfileRepository.findStudentByEmail("student@example.com")).thenReturn(Optional.of(student));
        when(paymentRepository.findByStudentIdAndStatusOrderByCreatedAtDesc(1L, PaymentStatus.SUCCESS))
                .thenReturn(List.of(payment));

        List<StudentPaymentHistoryResponse> result = studentService.getMyPaymentHistory("student@example.com", PaymentStatus.SUCCESS);

        assertEquals(1, result.size());
        assertEquals(21L, result.get(0).getPaymentId());
        assertEquals(PaymentStatus.SUCCESS, result.get(0).getPaymentStatus());
        assertEquals(TuitionStatus.PENDING, result.get(0).getTuition().getTuitionStatus());
    }

    @Test
    void getProfileShouldMapStudentInformation() {
        StudentProfile student = buildStudent();
        student.getUser().setFullName("Nguyen Van A");
        student.getUser().setPhone("0123456789");

        when(studentProfileRepository.findStudentByEmail("student@example.com")).thenReturn(Optional.of(student));

        StudentProfileResponse result = studentService.getProfile("student@example.com");

        assertEquals("Nguyen Van A", result.getUser().getFullName());
        assertEquals("0123456789", result.getUser().getPhone());
        assertEquals("20265098", result.getAcademic().getStudentCode());
    }

    @Test
    void updateProfileShouldRejectInvalidPhoneNumber() {
        StudentProfile student = buildStudent();
        UpdateProfileRequest request = new UpdateProfileRequest();
        ReflectionTestUtils.setField(request, "phone", "123");

        when(studentProfileRepository.findStudentByEmail("student@example.com")).thenReturn(Optional.of(student));

        assertThrows(UpdateFailException.class, () -> studentService.updateProfile("student@example.com", request));
    }

    @Test
    void getMyScheduleShouldReturnEmptyResponseWhenStudentHasNoEnrollments() {
        StudentProfile student = buildStudent();
        when(studentProfileRepository.findStudentByEmail("student@example.com")).thenReturn(Optional.of(student));
        when(enrollmentRepository.findByStudentIdAndSemesterIdAndStatus(student.getId(), null, EnrollmentStatus.ENROLLED))
                .thenReturn(List.of());

        var result = studentService.getMySchedule("student@example.com", null, null, null);

        assertNull(result.getTimetable());
    }

    @Test
    void uploadAvatarShouldRejectNonImageFile() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "hello".getBytes()
        );

        assertThrows(UpdateFailException.class, () -> studentService.uploadAvatar("student@example.com", file));
    }

    private StudentProfile buildStudent() {
        User user = new User();
        user.setEmail("student@example.com");
        user.setFullName("Student Test");
        user.setPersonalEmail("student.personal@example.com");
        ReflectionTestUtils.setField(user, "id", 1L);

        Department department = new Department();
        department.setName("CNTT");
        ReflectionTestUtils.setField(department, "id", 10L);

        Program program = new Program();
        program.setName("KTPM");
        ReflectionTestUtils.setField(program, "id", 20L);

        StudentProfile student = new StudentProfile();
        student.setUser(user);
        student.setStudentCode("20265098");
        student.setDepartment(department);
        student.setProgram(program);
        student.setClassName("SE01");
        student.setEnrollmentYear(2025);
        ReflectionTestUtils.setField(student, "id", 2L);
        return student;
    }

    private TuitionFee buildTuition(User user, Long tuitionId, Long semesterId) {
        AcademicYear academicYear = new AcademicYear();
        academicYear.setName("2025-2026");

        Semester semester = new Semester();
        semester.setName(SemesterName.HK1);
        semester.setSemesterNumber(1);
        semester.setAcademicYear(academicYear);
        ReflectionTestUtils.setField(semester, "id", semesterId);

        TuitionFee tuition = new TuitionFee();
        tuition.setStudent(user);
        tuition.setSemester(semester);
        tuition.setAmount(1_600_000D);
        tuition.setDiscount(0D);
        tuition.setFinalAmount(1_600_000D);
        tuition.setDueDate(LocalDate.of(2026, 6, 30));
        tuition.setStatus(TuitionStatus.PENDING);
        ReflectionTestUtils.setField(tuition, "id", tuitionId);
        return tuition;
    }
}
