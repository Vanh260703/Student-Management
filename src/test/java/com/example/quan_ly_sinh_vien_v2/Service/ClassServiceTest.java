package com.example.quan_ly_sinh_vien_v2.Service;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.Class.CreateClassRequest;
import com.example.quan_ly_sinh_vien_v2.Exception.CreateFailException;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.ClassEntity;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Department;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Semester;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Subject;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.TeacherProfile;
import com.example.quan_ly_sinh_vien_v2.Repository.ClassEntityRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.ClassScheduleRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.EnrollmentRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.SemesterRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.SubjectRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.TeacherProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class ClassServiceTest {
    @Mock private ClassEntityRepository classEntityRepository;
    @Mock private SemesterRepository semesterRepository;
    @Mock private SubjectRepository subjectRepository;
    @Mock private TeacherProfileRepository teacherProfileRepository;
    @Mock private EnrollmentRepository enrollmentRepository;
    @Mock private ClassScheduleRepository classScheduleRepository;

    private ClassService classService;

    @BeforeEach
    void setUp() {
        classService = new ClassService(
                classEntityRepository,
                semesterRepository,
                subjectRepository,
                teacherProfileRepository,
                enrollmentRepository,
                classScheduleRepository
        );
    }

    @Test
    void createClassShouldPersistWhenDepartmentMatchesAndSemesterActive() {
        Department department = new Department();
        ReflectionTestUtils.setField(department, "id", 1L);

        Semester semester = new Semester();
        semester.setIsActive(true);

        Subject subject = new Subject();
        subject.setDepartment(department);

        TeacherProfile teacher = new TeacherProfile();
        teacher.setDepartment(department);

        CreateClassRequest request = new CreateClassRequest();
        ReflectionTestUtils.setField(request, "semesterId", 10L);
        ReflectionTestUtils.setField(request, "subjectId", 20L);
        ReflectionTestUtils.setField(request, "teacherId", 30L);
        ReflectionTestUtils.setField(request, "classCode", "SE123");
        ReflectionTestUtils.setField(request, "maxStudents", 40);
        ReflectionTestUtils.setField(request, "room", "A1");

        when(semesterRepository.findById(10L)).thenReturn(Optional.of(semester));
        when(subjectRepository.findById(20L)).thenReturn(Optional.of(subject));
        when(teacherProfileRepository.findById(30L)).thenReturn(Optional.of(teacher));
        when(classEntityRepository.existsClassEntitiesByClassCode("SE123")).thenReturn(false);
        when(classEntityRepository.save(any(ClassEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClassEntity result = classService.createClass(request);

        ArgumentCaptor<ClassEntity> captor = ArgumentCaptor.forClass(ClassEntity.class);
        verify(classEntityRepository).save(captor.capture());
        assertEquals("SE123", captor.getValue().getClassCode());
        assertEquals(0, captor.getValue().getCurrentStudents());
        assertEquals("A1", result.getRoom());
    }

    @Test
    void createClassShouldRejectWhenTeacherAndSubjectDepartmentDiffer() {
        Department departmentOne = new Department();
        ReflectionTestUtils.setField(departmentOne, "id", 1L);
        Department departmentTwo = new Department();
        ReflectionTestUtils.setField(departmentTwo, "id", 2L);

        Semester semester = new Semester();
        semester.setIsActive(true);

        Subject subject = new Subject();
        subject.setDepartment(departmentOne);

        TeacherProfile teacher = new TeacherProfile();
        teacher.setDepartment(departmentTwo);

        CreateClassRequest request = new CreateClassRequest();
        ReflectionTestUtils.setField(request, "semesterId", 10L);
        ReflectionTestUtils.setField(request, "subjectId", 20L);
        ReflectionTestUtils.setField(request, "teacherId", 30L);
        ReflectionTestUtils.setField(request, "classCode", "SE123");

        when(semesterRepository.findById(10L)).thenReturn(Optional.of(semester));
        when(subjectRepository.findById(20L)).thenReturn(Optional.of(subject));
        when(teacherProfileRepository.findById(30L)).thenReturn(Optional.of(teacher));

        assertThrows(CreateFailException.class, () -> classService.createClass(request));
    }
}
