package com.example.quan_ly_sinh_vien_v2.Service.Admin;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.Admin.UpdateClassRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.ClassResponse;
import com.example.quan_ly_sinh_vien_v2.Exception.NotFoundException;
import com.example.quan_ly_sinh_vien_v2.Exception.UpdateFailException;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.ClassEntity;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Semester;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Subject;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.TeacherProfile;
import com.example.quan_ly_sinh_vien_v2.Repository.ClassEntityRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.SemesterRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.SubjectRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.TeacherProfileRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public class AdminClassService {
    private final ClassEntityRepository classEntityRepository;
    private final SemesterRepository semesterRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherProfileRepository teacherProfileRepository;

    public AdminClassService(ClassEntityRepository classEntityRepository, SemesterRepository semesterRepository, SubjectRepository subjectRepository, TeacherProfileRepository teacherProfileRepository) {
        this.classEntityRepository = classEntityRepository;
        this.semesterRepository = semesterRepository;
        this.subjectRepository = subjectRepository;
        this.teacherProfileRepository = teacherProfileRepository;
    }

    public ClassResponse updateClass(Long classId, @Valid UpdateClassRequest request) {
        ClassEntity classEntity = classEntityRepository.findById(classId)
                .orElseThrow(() -> new NotFoundException("Class not found!"));

        if (request.getSemesterId() != null) {
            Semester semester = semesterRepository.findById(request.getSemesterId())
                    .orElseThrow(() -> new NotFoundException("Semester not found!"));

            if (!semester.getIsActive()) {
                throw new UpdateFailException("Semester is not active!");
            }

            classEntity.setSemester(semester);
        }

        Subject subject = classEntity.getSubject();

        if (request.getSubjectId() != null) {
            subject = subjectRepository.findById(request.getSubjectId())
                    .orElseThrow(() -> new NotFoundException("Subject not found!"));

            classEntity.setSubject(subject);
        }

        if (request.getTeacherId() != null) {

            TeacherProfile teacher = teacherProfileRepository.findById(request.getTeacherId())
                    .orElseThrow(() -> new NotFoundException("Teacher not found!"));

            if (!teacher.getDepartment().getId().equals(subject.getDepartment().getId())) {
                throw new UpdateFailException("Teacher and subject must belong to same department!");
            }

            classEntity.setTeacher(teacher);
        }

        if (request.getClassCode() != null && !request.getClassCode().isBlank()) {
            classEntity.setClassCode(request.getClassCode().trim());
        }

        if (request.getMaxStudents() != null) {

            if (request.getMaxStudents() < classEntity.getCurrentStudents()) {
                throw new UpdateFailException("Max students cannot be less than current students!");
            }

            classEntity.setMaxStudents(request.getMaxStudents());
        }

        if (request.getRoom() != null) {
            classEntity.setRoom(request.getRoom());
        }

        if (request.getStatus() != null) {
            classEntity.setStatus(request.getStatus());
        }

        ClassEntity saved = classEntityRepository.save(classEntity);

        return ClassResponse.from(saved);
    }
}
