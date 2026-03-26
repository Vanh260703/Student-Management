package com.example.quan_ly_sinh_vien_v2.Service;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.Subject.CreateSubjectRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Request.Subject.UpdateSubjectRequest;
import com.example.quan_ly_sinh_vien_v2.Exception.AlreadyExistsException;
import com.example.quan_ly_sinh_vien_v2.Exception.CreateFailException;
import com.example.quan_ly_sinh_vien_v2.Exception.NotFoundException;
import com.example.quan_ly_sinh_vien_v2.Exception.UpdateFailException;
import com.example.quan_ly_sinh_vien_v2.Helper;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Department;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Subject;
import com.example.quan_ly_sinh_vien_v2.Repository.DepartmentRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.ProgramSubjectRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.SubjectRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectService {
    private final SubjectRepository subjectRepository;
    private final DepartmentRepository departmentRepository;
    private final ProgramSubjectRepository programSubjectRepository;

    public SubjectService(SubjectRepository subjectRepository, DepartmentRepository departmentRepository, ProgramSubjectRepository programSubjectRepository) {
        this.subjectRepository = subjectRepository;
        this.departmentRepository = departmentRepository;
        this.programSubjectRepository = programSubjectRepository;
    }

    // Get subjects (filter: departmentId, code, name, isActive, credits)
    public List<Subject> getSubjects(Long departmentId, String search, Boolean isActive, Integer credits) {
        String keyword = null;

        if (search != null && !search.trim().isEmpty()) {
            keyword = search.trim().toLowerCase();
        }

        return subjectRepository.searchSubjects(
                departmentId,
                keyword,
                isActive,
                credits
        );
    }

    // Create subject
    public Subject createSubject(@Valid CreateSubjectRequest request) {
        if (request.getDepartmentId() == null || request.getCode() == null || request.getName() == null || request.getCredits() == null || request.getIsActive() == null) {
            throw new CreateFailException("Create subject fail!");
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new NotFoundException("Department not found!"));

        if (subjectRepository.existsSubjectByCode(request.getCode())) {
            throw new AlreadyExistsException("Create subject fail!");
        }

        Subject subject = new Subject();

        subject.setDepartment(department);
        subject.setCode(request.getCode());
        subject.setName(request.getName());
        subject.setNormalizeName(Helper.normalizeFullName(request.getName()));
        subject.setCredits(request.getCredits());
        subject.setIsActive(request.getIsActive());

        if (request.getDescription() != null) {
            subject.setDescription(request.getDescription());
        }

        subjectRepository.save(subject);

        return subject;
    }

    // Get details subject
    public Subject getDetailsSubject(Long subjectId) {
        return subjectRepository.findById(subjectId).orElseThrow(() -> new NotFoundException("Subject not found!"));
    }

    // Update subject
    @Transactional
    public Subject updateSubject(Long subjectId, @Valid UpdateSubjectRequest request) {
        Subject subject = subjectRepository.findById(subjectId).orElseThrow(() -> new NotFoundException("Subject not found!"));

        if (request.getName() == null && request.getIsActive() == null && request.getCredits() == null && request.getDescription() == null) {
            throw new UpdateFailException("Update subject fail!");
        }

        if (request.getName() != null) {
            subject.setName(request.getName());
            subject.setNormalizeName(Helper.normalizeFullName(request.getName()));
        }

        if (request.getDescription() != null) {
            subject.setDescription(request.getDescription());
        }

        if (request.getIsActive() != null) {
            subject.setIsActive(request.getIsActive());
        }

        if (request.getCredits() != null) {
            subject.setCredits(request.getCredits());
        }

        return subject;
    }

    @Transactional
    public void deleteSubject(Long subjectId) {
        if (!subjectRepository.existsById(subjectId)) {
            throw new NotFoundException("Subject not found!");
        }

        if (programSubjectRepository.existsProgramSubjectBySubjectId(subjectId)) {
            programSubjectRepository.deleteBySubjectId(subjectId);
        }

        subjectRepository.deleteById(subjectId);
    }
}
