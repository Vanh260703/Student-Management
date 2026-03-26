package com.example.quan_ly_sinh_vien_v2.Service;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.Program.CreateProgramRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Request.Program.CreateProgramSubjectRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Request.Program.UpdateProgramRequest;
import com.example.quan_ly_sinh_vien_v2.Exception.AlreadyExistsException;
import com.example.quan_ly_sinh_vien_v2.Exception.CreateFailException;
import com.example.quan_ly_sinh_vien_v2.Exception.NotFoundException;
import com.example.quan_ly_sinh_vien_v2.Exception.UpdateFailException;
import com.example.quan_ly_sinh_vien_v2.Helper;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Department;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Program;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.ProgramSubject;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Subject;
import com.example.quan_ly_sinh_vien_v2.Repository.DepartmentRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.ProgramRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.ProgramSubjectRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.SubjectRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProgramService {
    private final ProgramRepository programRepository;
    private final DepartmentRepository departmentRepository;
    private final ProgramSubjectRepository programSubjectRepository;
    private final SubjectRepository subjectRepository;

    public ProgramService(ProgramRepository programRepository, DepartmentRepository departmentRepository, ProgramSubjectRepository programSubjectRepository, SubjectRepository subjectRepository) {
        this.programRepository = programRepository;
        this.departmentRepository = departmentRepository;
        this.programSubjectRepository = programSubjectRepository;
        this.subjectRepository = subjectRepository;
    }

    // List programs
    public List<Program> getPrograms(Long departmentId, String search) {
        String keyword = null;

        if (search != null && !search.trim().isEmpty()) {
            keyword = search.trim().toLowerCase();
        }

        return programRepository.searchPrograms(departmentId, keyword);
    }


    // Create program
    public Program createProgram(@Valid CreateProgramRequest request) {
        if (request.getCode() == null || request.getName() == null || request.getDepartmentId() == null || request.getTotalCredits() == null || request.getDurationYear() == null) {
            throw new CreateFailException("Create program fail!");
        }

        if (programRepository.existsProgramByCode(request.getCode())) {
            throw new AlreadyExistsException("Program already exists");
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new NotFoundException("Department not found!"));



        Program program = new Program();

        program.setCode(request.getCode());
        program.setName(request.getName());
        program.setNormalizeName(Helper.normalizeFullName(request.getName()));
        program.setDepartment(department);
        program.setTotalCredits(request.getTotalCredits());
        program.setDurationYears(request.getDurationYear());

        if (request.getDescription() != null) {
            program.setDescription(request.getDescription());
        }

        programRepository.save(program);

        return program;
    }


    // Get details program
    public Program getDetailsProgram(Long programId) {
        return programRepository.findById(programId).orElseThrow(() -> new NotFoundException("Program not found!"));
    }

    // Update program
    @Transactional
    public Program updateProgram(Long programId, @Valid UpdateProgramRequest request) {
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new NotFoundException("Program not found!"));

        if (request.getName() == null && request.getDescription() == null && request.getDurationYear() == null && request.getTotalCredits() == null) {
            throw new UpdateFailException("Update program fail!");
        }

        if (request.getName() != null) {
            program.setName(request.getName());
            program.setNormalizeName(Helper.normalizeFullName(request.getName()));
        }

        if (request.getDescription() != null) {
            program.setDescription(request.getDescription());
        }

        if (request.getTotalCredits() != null) {
            program.setTotalCredits(request.getTotalCredits());
        }

        if (request.getDurationYear() != null) {
            program.setDurationYears(request.getDurationYear());
        }

        return program;
    }

    // Delete program
    public void deleteProgram(Long programId) {
        if (!programRepository.existsById(programId)) {
            throw new NotFoundException("Program not found!");
        }

        programRepository.deleteById(programId);
    }

    // Get subjects in program (filter: semester, isRequired)
    public List<ProgramSubject> getSubjectsInProgram(Long programId, Integer semester, Boolean isRequired) {
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new NotFoundException("Program not found!"));

        if (semester != null && isRequired != null) {
            return programSubjectRepository.findAllByProgramAndSemesterAndIsRequired(program, semester, isRequired);
        }

        if (semester != null) {
            return programSubjectRepository.findAllByProgramAndSemester(program, semester);
        }

        if (isRequired != null) {
            return programSubjectRepository.findAllByProgramAndIsRequired(program, isRequired);
        }

        return programSubjectRepository.findAllByProgram(program);
    }

    // Add subject in program
    public ProgramSubject addSubjectInProgram(Long programId, @Valid CreateProgramSubjectRequest request) {
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new NotFoundException("Program not found!"));

        if (request.getSubjectId() == null || request.getSemester() == null) {
            throw new CreateFailException("Add subject in program fail!");
        }

        if (programSubjectRepository.existsProgramSubjectBySubjectId(request.getSubjectId())) {
            throw new CreateFailException("Add subject in program fail!");
        }

        Subject subject = subjectRepository.findById(request.getSubjectId()).orElseThrow(() -> new NotFoundException("Subject not found!"));

        ProgramSubject programSubject = new ProgramSubject();

        programSubject.setSubject(subject);
        programSubject.setProgram(program);
        programSubject.setSemester(request.getSemester());

        if (request.getIsRequired() != null) {
            programSubject.setIsRequired(request.getIsRequired());
        }

        if (request.getPrerequisiteSubjectId() != null) {
            Subject prerequisiteSubject = subjectRepository.findById(request.getPrerequisiteSubjectId()).orElseThrow(() -> new NotFoundException("Subject not found!"));

            programSubject.setPrerequisiteSubject(prerequisiteSubject);
        }

        programSubjectRepository.save(programSubject);

        return programSubject;
    }

    @Transactional
    public void deleteSubjectInProgram(Long programId, Long subjectId) {
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new NotFoundException("Program not found!"));

        if (!programSubjectRepository.existsProgramSubjectByProgramAndSubjectId(program, subjectId)) {
            throw new NotFoundException("Subject not found!");
        }

        programSubjectRepository.deleteBySubjectId(subjectId);
    }
}
