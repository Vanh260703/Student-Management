package com.example.quan_ly_sinh_vien_v2.Service;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.Teacher.CreateGradeComponentRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Request.Teacher.UpdateClassRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.ClassResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.GradeComponentResponse;
import com.example.quan_ly_sinh_vien_v2.Exception.CreateFailException;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.AcademicYear;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.ClassEntity;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Department;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.GradeComponent;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Semester;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Subject;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.TeacherProfile;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.User;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.ClassStatus;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.GradeComponentType;
import com.example.quan_ly_sinh_vien_v2.Repository.AttendanceRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.ClassEntityRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.EnrollmentRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.GradeComponentRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.GradeRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.TeacherProfileRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {
    @Mock
    private ClassEntityRepository classEntityRepository;
    @Mock
    private TeacherProfileRepository teacherProfileRepository;
    @Mock
    private AttendanceRepository attendanceRepository;
    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private GradeComponentRepository gradeComponentRepository;
    @Mock
    private GradeRepository gradeRepository;
    @Mock
    private EnrollmentService enrollmentService;

    private TeacherService teacherService;

    @BeforeEach
    void setUp() {
        teacherService = new TeacherService(
                classEntityRepository,
                teacherProfileRepository,
                attendanceRepository,
                enrollmentRepository,
                userRepository,
                gradeComponentRepository,
                gradeRepository,
                enrollmentService
        );
    }

    @Test
    void updateClassShouldRejectWhenTeacherDoesNotOwnClass() {
        TeacherProfile owner = buildTeacher(1L, "owner@example.com", "GV001");
        TeacherProfile anotherTeacher = buildTeacher(2L, "teacher@example.com", "GV002");
        ClassEntity classEntity = buildClassEntity(owner);

        UpdateClassRequest request = new UpdateClassRequest();
        ReflectionTestUtils.setField(request, "room", "B3");

        when(classEntityRepository.findById(1L)).thenReturn(Optional.of(classEntity));
        when(teacherProfileRepository.findTeacherProfileByEmail("teacher@example.com"))
                .thenReturn(Optional.of(anotherTeacher));

        assertThrows(AuthorizationDeniedException.class,
                () -> teacherService.updateClass("teacher@example.com", 1L, request));
    }

    @Test
    void updateClassShouldUpdateRoomWhenTeacherOwnsClass() {
        TeacherProfile teacher = buildTeacher(1L, "teacher@example.com", "GV001");
        ClassEntity classEntity = buildClassEntity(teacher);

        UpdateClassRequest request = new UpdateClassRequest();
        ReflectionTestUtils.setField(request, "room", "B3");

        when(classEntityRepository.findById(1L)).thenReturn(Optional.of(classEntity));
        when(teacherProfileRepository.findTeacherProfileByEmail("teacher@example.com"))
                .thenReturn(Optional.of(teacher));

        ClassResponse response = teacherService.updateClass("teacher@example.com", 1L, request);

        assertEquals("B3", classEntity.getRoom());
        assertEquals("B3", response.getTeacherResponse() == null ? classEntity.getRoom() : classEntity.getRoom());
    }

    @Test
    void createGradeComponentShouldRejectWhenTotalWeightExceedsOneHundred() {
        TeacherProfile teacher = buildTeacher(1L, "teacher@example.com", "GV001");
        ClassEntity classEntity = buildClassEntity(teacher);

        GradeComponent existing = new GradeComponent();
        existing.setWeight(80);

        CreateGradeComponentRequest request = new CreateGradeComponentRequest();
        ReflectionTestUtils.setField(request, "type", GradeComponentType.FINAL);
        ReflectionTestUtils.setField(request, "weight", 30);
        ReflectionTestUtils.setField(request, "maxScore", 10D);

        User user = new User();
        user.setRole(com.example.quan_ly_sinh_vien_v2.Modal.Enum.Role.ROLE_TEACHER);

        when(classEntityRepository.findById(1L)).thenReturn(Optional.of(classEntity));
        when(userRepository.findByEmail("teacher@example.com")).thenReturn(Optional.of(user));
        when(teacherProfileRepository.findTeacherProfileByEmail("teacher@example.com"))
                .thenReturn(Optional.of(teacher));
        when(gradeComponentRepository.existsGradeComponentByClassEntityIdAndType(1L, GradeComponentType.FINAL))
                .thenReturn(false);
        when(gradeComponentRepository.findAllByClassEntityId(1L)).thenReturn(List.of(existing));

        assertThrows(CreateFailException.class,
                () -> teacherService.createGradeComponent("teacher@example.com", 1L, request));
    }

    @Test
    void createGradeComponentShouldPersistWhenWeightValid() {
        TeacherProfile teacher = buildTeacher(1L, "teacher@example.com", "GV001");
        ClassEntity classEntity = buildClassEntity(teacher);

        CreateGradeComponentRequest request = new CreateGradeComponentRequest();
        ReflectionTestUtils.setField(request, "type", GradeComponentType.MIDTERM);
        ReflectionTestUtils.setField(request, "weight", 40);
        ReflectionTestUtils.setField(request, "maxScore", 10D);

        User user = new User();
        user.setRole(com.example.quan_ly_sinh_vien_v2.Modal.Enum.Role.ROLE_TEACHER);

        when(classEntityRepository.findById(1L)).thenReturn(Optional.of(classEntity));
        when(userRepository.findByEmail("teacher@example.com")).thenReturn(Optional.of(user));
        when(teacherProfileRepository.findTeacherProfileByEmail("teacher@example.com"))
                .thenReturn(Optional.of(teacher));
        when(gradeComponentRepository.existsGradeComponentByClassEntityIdAndType(1L, GradeComponentType.MIDTERM))
                .thenReturn(false);
        when(gradeComponentRepository.findAllByClassEntityId(1L)).thenReturn(List.of());
        when(gradeComponentRepository.save(any(GradeComponent.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GradeComponentResponse response = teacherService.createGradeComponent("teacher@example.com", 1L, request);

        verify(gradeComponentRepository).save(any(GradeComponent.class));
        assertEquals(GradeComponentType.MIDTERM, response.getType());
        assertEquals(40, response.getWeight());
        assertEquals("Giữa kỳ", response.getName());
    }

    private TeacherProfile buildTeacher(Long id, String email, String teacherCode) {
        Department department = new Department();
        ReflectionTestUtils.setField(department, "id", 1L);
        department.setName("CNTT");

        User user = new User();
        user.setEmail(email);
        user.setFullName("Teacher");

        TeacherProfile teacher = new TeacherProfile();
        ReflectionTestUtils.setField(teacher, "id", id);
        teacher.setDepartment(department);
        teacher.setTeacherCode(teacherCode);
        teacher.setDegree("Master");
        teacher.setSpecialization("Software");
        teacher.setUser(user);
        return teacher;
    }

    private ClassEntity buildClassEntity(TeacherProfile teacher) {
        AcademicYear academicYear = new AcademicYear();
        academicYear.setName("2025-2026");

        Semester semester = new Semester();
        semester.setAcademicYear(academicYear);
        semester.setIsActive(true);

        Department department = new Department();
        ReflectionTestUtils.setField(department, "id", 1L);
        department.setName("CNTT");

        Subject subject = new Subject();
        subject.setDepartment(department);
        subject.setCode("IT101");
        subject.setName("Nhap mon");
        subject.setCredits(3);

        ClassEntity classEntity = new ClassEntity();
        ReflectionTestUtils.setField(classEntity, "id", 1L);
        classEntity.setClassCode("IT101-01");
        classEntity.setSemester(semester);
        classEntity.setTeacher(teacher);
        classEntity.setSubject(subject);
        classEntity.setRoom("A1");
        classEntity.setStatus(ClassStatus.OPEN);
        return classEntity;
    }
}
