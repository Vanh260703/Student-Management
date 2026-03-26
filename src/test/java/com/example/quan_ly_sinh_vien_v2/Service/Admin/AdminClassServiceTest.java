package com.example.quan_ly_sinh_vien_v2.Service.Admin;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.Admin.UpdateClassRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.ClassResponse;
import com.example.quan_ly_sinh_vien_v2.Exception.UpdateFailException;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.AcademicYear;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.ClassEntity;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Department;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Semester;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Subject;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.TeacherProfile;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.User;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.ClassStatus;
import com.example.quan_ly_sinh_vien_v2.Repository.ClassEntityRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.SemesterRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.SubjectRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.TeacherProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminClassServiceTest {
    @Mock
    private ClassEntityRepository classEntityRepository;
    @Mock
    private SemesterRepository semesterRepository;
    @Mock
    private SubjectRepository subjectRepository;
    @Mock
    private TeacherProfileRepository teacherProfileRepository;

    private AdminClassService adminClassService;

    @BeforeEach
    void setUp() {
        adminClassService = new AdminClassService(
                classEntityRepository,
                semesterRepository,
                subjectRepository,
                teacherProfileRepository
        );
    }

    @Test
    void updateClassShouldRejectWhenMaxStudentsLessThanCurrentStudents() {
        ClassEntity classEntity = buildClassEntity();
        classEntity.setCurrentStudents(10);

        UpdateClassRequest request = new UpdateClassRequest();
        ReflectionTestUtils.setField(request, "maxStudents", 5);

        when(classEntityRepository.findById(1L)).thenReturn(Optional.of(classEntity));

        assertThrows(UpdateFailException.class, () -> adminClassService.updateClass(1L, request));
    }

    @Test
    void updateClassShouldPersistTeacherStatusAndRoomWhenRequestValid() {
        Department department = new Department();
        ReflectionTestUtils.setField(department, "id", 1L);
        department.setName("CNTT");

        Subject subject = new Subject();
        subject.setDepartment(department);
        subject.setName("CTDL");
        subject.setCode("IT101");
        subject.setCredits(3);

        TeacherProfile teacher = new TeacherProfile();
        ReflectionTestUtils.setField(teacher, "id", 4L);
        teacher.setDepartment(department);
        teacher.setTeacherCode("GV001");
        teacher.setDegree("Master");
        teacher.setSpecialization("Software");
        User teacherUser = new User();
        teacherUser.setFullName("Teacher A");
        teacherUser.setEmail("teacher@example.com");
        teacher.setUser(teacherUser);

        ClassEntity classEntity = buildClassEntity();
        classEntity.setSubject(subject);

        UpdateClassRequest request = new UpdateClassRequest();
        ReflectionTestUtils.setField(request, "teacherId", 4L);
        ReflectionTestUtils.setField(request, "room", "B2");
        ReflectionTestUtils.setField(request, "status", ClassStatus.CLOSE);

        when(classEntityRepository.findById(1L)).thenReturn(Optional.of(classEntity));
        when(teacherProfileRepository.findById(4L)).thenReturn(Optional.of(teacher));
        when(classEntityRepository.save(any(ClassEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClassResponse response = adminClassService.updateClass(1L, request);

        verify(classEntityRepository).save(classEntity);
        assertEquals("B2", classEntity.getRoom());
        assertEquals(ClassStatus.CLOSE, classEntity.getStatus());
        assertEquals("GV001", response.getTeacherResponse().getTeacherCode());
    }

    private ClassEntity buildClassEntity() {
        Department department = new Department();
        ReflectionTestUtils.setField(department, "id", 1L);
        department.setName("CNTT");

        AcademicYear academicYear = new AcademicYear();
        academicYear.setName("2025-2026");

        Semester semester = new Semester();
        semester.setIsActive(true);
        semester.setAcademicYear(academicYear);

        Subject subject = new Subject();
        subject.setDepartment(department);
        subject.setName("Nhap mon");
        subject.setCode("IT100");
        subject.setCredits(3);

        User teacherUser = new User();
        teacherUser.setFullName("Teacher Old");
        teacherUser.setEmail("old-teacher@example.com");

        TeacherProfile teacher = new TeacherProfile();
        ReflectionTestUtils.setField(teacher, "id", 2L);
        teacher.setTeacherCode("GV000");
        teacher.setDegree("Bachelor");
        teacher.setSpecialization("CS");
        teacher.setDepartment(department);
        teacher.setUser(teacherUser);

        ClassEntity classEntity = new ClassEntity();
        classEntity.setClassCode("IT100-01");
        classEntity.setSemester(semester);
        classEntity.setSubject(subject);
        classEntity.setTeacher(teacher);
        classEntity.setRoom("A1");
        classEntity.setStatus(ClassStatus.OPEN);

        return classEntity;
    }
}
