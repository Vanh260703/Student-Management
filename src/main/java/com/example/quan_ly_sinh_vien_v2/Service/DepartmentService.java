package com.example.quan_ly_sinh_vien_v2.Service;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.Department.CreateDepartmentRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Request.Department.UpdateDepartmentRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.StudentResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.TeacherResponse;
import com.example.quan_ly_sinh_vien_v2.Exception.CreateFailException;
import com.example.quan_ly_sinh_vien_v2.Exception.NotFoundException;
import com.example.quan_ly_sinh_vien_v2.Exception.UpdateFailException;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Department;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.StudentProfile;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.TeacherProfile;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.StudentStatus;
import com.example.quan_ly_sinh_vien_v2.Repository.DepartmentRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.StudentProfileRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.TeacherProfileRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final TeacherProfileRepository teacherProfileRepository;
    private final StudentProfileRepository studentProfileRepository;

    public DepartmentService(DepartmentRepository departmentRepository, TeacherProfileRepository teacherProfileRepository, StudentProfileRepository studentProfileRepository) {
        this.departmentRepository = departmentRepository;
        this.teacherProfileRepository = teacherProfileRepository;
        this.studentProfileRepository = studentProfileRepository;
    }

    // Create department (ADMIN ONLY)
    public Department createDepartment(@Valid CreateDepartmentRequest request) {
        if (request.getCode() == null || request.getName() == null) {
            throw new CreateFailException("Create department fail!");
        }

        if (departmentRepository.existsDepartmentByCode(request.getCode())) {
            throw new CreateFailException("Create department fail!");
        }

        Department department = new Department();

        department.setCode(request.getCode());
        department.setName(request.getName());

        if (request.getDescription() != null) {
            department.setDescription(request.getDescription());
        }

        if (request.getHeadTeacherId() != null) {
            TeacherProfile teacher = teacherProfileRepository.findById(request.getHeadTeacherId())
                    .orElseThrow(() -> new NotFoundException("Teacher not found!"));

            department.setHeadTeacher(teacher);
        }

        departmentRepository.save(department);

        return department;
    }

    // Get details department (ALL)
    public Department getDetailsDepartment(Long departmentId) {
        return departmentRepository.findById(departmentId).orElseThrow(() -> new NotFoundException("Department not found!"));
    }

    // Update department (ADMIN ONLY)
    @Transactional
    public Department updateDepartment(Long departmentId, @Valid UpdateDepartmentRequest request) {
        Department department = departmentRepository.findById(departmentId).orElseThrow(() -> new NotFoundException("Department not found!"));

        if (request.getName() == null && request.getDescription() == null && request.getHeadTeacherId() == null) {
            throw new UpdateFailException("Update department fail!");
        }

        if (request.getName() != null) {
            department.setName(request.getName());
        }

        if (request.getDescription() != null) {
            department.setDescription(request.getDescription());
        }

        if (request.getHeadTeacherId() != null) {
            TeacherProfile teacher = teacherProfileRepository.findById(request.getHeadTeacherId())
                    .orElseThrow(() -> new NotFoundException("Teacher not found!"));

            department.setHeadTeacher(teacher);
        }

        return department;
    }

    // Delete department
    @Transactional
    public void deleteDepartment(Long departmentId) {
        if (!departmentRepository.existsById(departmentId)) {
            throw new NotFoundException("Department not found!");
        }

        departmentRepository.deleteById(departmentId);
    }

    // List teachers in department
    public List<TeacherResponse> getTeachersInDepartment(Long departmentId, String search, String degree) {
        Department department = departmentRepository.findById(departmentId).orElseThrow(() -> new NotFoundException("Department not found!"));

        String keyword = null;

        if (search != null && !search.trim().isEmpty()) {
            keyword = search.trim().toLowerCase();
        }


        List<TeacherProfile> teachers = teacherProfileRepository.searchTeachers(
                department.getId(),
                keyword,
                (degree != null && !degree.isBlank()) ? degree : null
        );

        return teachers.stream().map(TeacherResponse::from).toList();
    }

    // List students in department
    public List<StudentResponse> getStudentsInDepartment(Long departmentId, String search, Long programId, Integer enrollmentYear, StudentStatus status) {

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new NotFoundException("Department not found!"));

        String keyword = null;

        if (search != null && !search.trim().isEmpty()) {
            keyword = search.trim().toLowerCase();
        }

        List<StudentProfile> students = studentProfileRepository.searchStudents(
                department.getId(),
                keyword,
                programId,
                enrollmentYear,
                status,
                null,
                 null
        );

        return students.stream().map(StudentResponse::from).toList();
    }
}
