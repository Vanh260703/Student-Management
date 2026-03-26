package com.example.quan_ly_sinh_vien_v2.Repository;

import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Program;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.ProgramSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramSubjectRepository extends JpaRepository<ProgramSubject, Long> {
    List<ProgramSubject> findAllByProgramAndSemesterAndIsRequired(Program program, Integer semester, Boolean isRequired);

    List<ProgramSubject> findAllByProgramAndSemester(Program program, Integer semester);

    List<ProgramSubject> findAllByProgramAndIsRequired(Program program, Boolean isRequired);

    List<ProgramSubject> findAllByProgram(Program program);

    boolean existsProgramSubjectBySubjectId(Long subjectId);

    boolean existsProgramSubjectByProgramAndSubjectId(Program program, Long subjectId);

    void deleteBySubjectId(Long subjectId);

    List<ProgramSubject> findAllWithSubjectsByProgram(Program program);
}
