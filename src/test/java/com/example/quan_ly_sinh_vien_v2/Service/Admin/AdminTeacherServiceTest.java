package com.example.quan_ly_sinh_vien_v2.Service.Admin;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.Admin.CreateTeacherRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.TeacherResponse;
import com.example.quan_ly_sinh_vien_v2.Exception.AlreadyExistsException;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Department;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.TeacherProfile;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.User;
import com.example.quan_ly_sinh_vien_v2.Repository.DepartmentRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.TeacherProfileRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.UserRepository;
import com.example.quan_ly_sinh_vien_v2.Service.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminTeacherServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private TeacherProfileRepository teacherProfileRepository;
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private MailService mailService;
    @Mock
    private PasswordEncoder passwordEncoder;

    private AdminTeacherService adminTeacherService;

    @BeforeEach
    void setUp() {
        adminTeacherService = new AdminTeacherService(
                userRepository,
                teacherProfileRepository,
                departmentRepository,
                mailService
        );
        ReflectionTestUtils.setField(adminTeacherService, "passwordEncoder", passwordEncoder);
    }

    @Test
    void createTeacherShouldRejectWhenPersonalEmailExists() {
        CreateTeacherRequest request = new CreateTeacherRequest();
        ReflectionTestUtils.setField(request, "personalEmail", "teacher@example.com");

        when(userRepository.existsUserByPersonalEmail("teacher@example.com")).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> adminTeacherService.createTeacher(request));
    }

    @Test
    void createTeacherShouldPersistTeacherAndSendMail() {
        Department department = new Department();
        ReflectionTestUtils.setField(department, "id", 1L);

        CreateTeacherRequest request = new CreateTeacherRequest();
        ReflectionTestUtils.setField(request, "fullName", "Teacher A");
        ReflectionTestUtils.setField(request, "personalEmail", "teacher@example.com");
        ReflectionTestUtils.setField(request, "phone", "0911222333");
        ReflectionTestUtils.setField(request, "teacherCode", "GV001");
        ReflectionTestUtils.setField(request, "departmentId", 1L);
        ReflectionTestUtils.setField(request, "degree", "Master");
        ReflectionTestUtils.setField(request, "address", "HCM");

        when(userRepository.existsUserByPersonalEmail("teacher@example.com")).thenReturn(false);
        when(teacherProfileRepository.existsTeacherProfileByTeacherCode("GV001")).thenReturn(false);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(passwordEncoder.encode(any(String.class))).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(teacherProfileRepository.save(any(TeacherProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TeacherResponse response = adminTeacherService.createTeacher(request);

        verify(userRepository).save(any(User.class));
        verify(teacherProfileRepository).save(any(TeacherProfile.class));
        verify(mailService).sendEmail(eq("teacher@example.com"), contains("Cấp tài khoản"), any(String.class));
        assertEquals("GV001", response.getTeacherCode());
        assertEquals("Master", response.getDegree());
    }
}
