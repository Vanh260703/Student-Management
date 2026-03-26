package com.example.quan_ly_sinh_vien_v2.Service.Admin;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.Admin.CreateStudentRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Request.Admin.UpdateStudentRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.StudentProfileResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.StudentResponse;
import com.example.quan_ly_sinh_vien_v2.Exception.UpdateFailException;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Department;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Program;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.StudentProfile;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.User;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.Gender;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.StudentStatus;
import com.example.quan_ly_sinh_vien_v2.Repository.DepartmentRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.ProgramRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.StudentProfileRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.UserRepository;
import com.example.quan_ly_sinh_vien_v2.Service.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminStudentServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private StudentProfileRepository studentProfileRepository;
    @Mock
    private ProgramRepository programRepository;
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private MailService mailService;
    @Mock
    private PasswordEncoder passwordEncoder;

    private AdminStudentService adminStudentService;

    @BeforeEach
    void setUp() {
        adminStudentService = new AdminStudentService(
                userRepository,
                studentProfileRepository,
                programRepository,
                departmentRepository,
                mailService
        );
        ReflectionTestUtils.setField(adminStudentService, "passwordEncoder", passwordEncoder);
    }

    @Test
    void createStudentShouldPersistStudentAndSendMail() {
        Department department = new Department();
        ReflectionTestUtils.setField(department, "id", 1L);
        department.setName("CNTT");

        Program program = new Program();
        ReflectionTestUtils.setField(program, "id", 2L);
        program.setName("Software Engineering");
        program.setDepartment(department);

        CreateStudentRequest request = new CreateStudentRequest();
        ReflectionTestUtils.setField(request, "fullName", "Nguyen Van A");
        ReflectionTestUtils.setField(request, "personalEmail", "a@example.com");
        ReflectionTestUtils.setField(request, "phone", "0911222333");
        ReflectionTestUtils.setField(request, "departmentId", 1L);
        ReflectionTestUtils.setField(request, "programId", 2L);
        ReflectionTestUtils.setField(request, "dayOfBirth", LocalDate.of(2005, 1, 1));
        ReflectionTestUtils.setField(request, "address", "HCM");
        ReflectionTestUtils.setField(request, "gender", Gender.MALE);
        ReflectionTestUtils.setField(request, "className", "SE01");

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(programRepository.findById(2L)).thenReturn(Optional.of(program));
        when(userRepository.existsUserByPersonalEmail("a@example.com")).thenReturn(false);
        when(passwordEncoder.encode(any(String.class))).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(studentProfileRepository.save(any(StudentProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StudentResponse response = adminStudentService.createStudent(request);

        verify(userRepository).save(any(User.class));
        verify(studentProfileRepository).save(any(StudentProfile.class));
        verify(mailService).sendEmail(eq("a@example.com"), contains("Cấp tài khoản"), any(String.class));
        assertEquals("SE01", response.getClassName());
        assertEquals(2L, response.getProgramId());
    }

    @Test
    void updateStudentShouldRejectInvalidPhone() {
        Department department = new Department();
        ReflectionTestUtils.setField(department, "id", 1L);
        department.setName("CNTT");

        Program program = new Program();
        ReflectionTestUtils.setField(program, "id", 2L);
        program.setName("Software Engineering");

        User user = new User();
        user.setEmail("sv@example.com");

        StudentProfile student = new StudentProfile();
        student.setUser(user);
        student.setDepartment(department);
        student.setProgram(program);
        student.setStudentCode("20260001");

        UpdateStudentRequest request = new UpdateStudentRequest();
        ReflectionTestUtils.setField(request, "phone", "123");

        when(studentProfileRepository.findById(1L)).thenReturn(Optional.of(student));

        assertThrows(UpdateFailException.class, () -> adminStudentService.updateStudent(1L, request));
    }

    @Test
    void updateStudentShouldPersistAcademicAndUserFields() {
        Department department = new Department();
        ReflectionTestUtils.setField(department, "id", 1L);
        department.setName("CNTT");

        Program program = new Program();
        ReflectionTestUtils.setField(program, "id", 2L);
        program.setName("Software Engineering");

        User user = new User();
        user.setEmail("sv@example.com");
        user.setFullName("Old Name");

        StudentProfile student = new StudentProfile();
        ReflectionTestUtils.setField(student, "id", 5L);
        student.setUser(user);
        student.setDepartment(department);
        student.setProgram(program);
        student.setStudentCode("20260001");
        student.setEnrollmentYear(2026);
        student.setClassName("SE01");
        student.setStatus(StudentStatus.ACTIVE);

        UpdateStudentRequest request = new UpdateStudentRequest();
        ReflectionTestUtils.setField(request, "fullName", "Nguyen Van B");
        ReflectionTestUtils.setField(request, "className", "SE02");
        ReflectionTestUtils.setField(request, "status", StudentStatus.SUSPENDED);
        ReflectionTestUtils.setField(request, "isActive", false);

        when(studentProfileRepository.findById(5L)).thenReturn(Optional.of(student));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(studentProfileRepository.save(any(StudentProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StudentProfileResponse response = adminStudentService.updateStudent(5L, request);

        assertEquals("Nguyen Van B", response.getUser().getFullName());
        assertEquals("SE02", response.getAcademic().getClassName());
        assertEquals(StudentStatus.SUSPENDED, response.getAcademic().getStatus());
        assertEquals(false, user.getIsActive());
    }
}
