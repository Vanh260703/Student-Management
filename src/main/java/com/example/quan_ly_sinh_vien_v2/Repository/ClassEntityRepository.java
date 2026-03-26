package com.example.quan_ly_sinh_vien_v2.Repository;

import com.example.quan_ly_sinh_vien_v2.Modal.Entity.ClassEntity;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Semester;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.StudentProfile;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.TeacherProfile;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.ClassStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Repository
public interface ClassEntityRepository extends JpaRepository<ClassEntity, Long> {
    @Query("""
    SELECT c FROM ClassEntity c
    WHERE (:semesterId IS NULL OR c.semester.id = :semesterId)
    AND (:subjectId IS NULL OR c.subject.id = :subjectId)
    AND (:teacherId IS NULL OR c.teacher.id = :teacherId)
    AND (:status IS NULL OR c.status = :status)
    AND (
        :search IS NULL
        OR LOWER(c.classCode) LIKE LOWER(CONCAT('%', :search, '%'))
        OR c.subject.normalizeName LIKE CONCAT('%', :search, '%')
    )
    AND (
        :hasSlot IS NULL
        OR (:hasSlot = TRUE AND c.currentStudents < c.maxStudents)
        OR (:hasSlot = FALSE AND c.currentStudents >= c.maxStudents)
    )
""")
    List<ClassEntity> searchClasses(
            @Param("semesterId") Long semesterId,
            @Param("subjectId") Long subjectId,
            @Param("teacherId") Long teacherId,
            @Param("status") ClassStatus status,
            @Param("search") String search,
            @Param("hasSlot") Boolean hasSlot
    );

    boolean existsClassEntitiesByClassCode(String classCode);

    List<ClassEntity> findAllByTeacher(TeacherProfile teacher);

    @Query("""
        SELECT c FROM ClassEntity c 
        WHERE c.status = "OPEN"
            AND (c.semester.id = :semesterId)
            AND (:subjectId IS NULL OR c.subject.id = :subjectId )
            AND (:departmentId IS NULL OR c.subject.department.id = :departmentId)
            AND (
                :search IS NULL OR 
                LOWER(c.subject.normalizeName) LIKE LOWER(CONCAT('%', :search, '%')) OR 
                LOWER(c.classCode) LIKE LOWER(CONCAT('%', :search, '%'))
                )
            AND (:hasSlot = false OR c.currentStudents < c.maxStudents)
            AND (:notEnrolled = false OR 
                 NOT EXISTS (
                    SELECT 1 FROM Enrollment e
                    WHERE e.classEntity.id = c.id
                    AND e.student.id = :studentId
                )
            )
    """)
    List<ClassEntity> findAvailableClasses(
            @Param("semesterId") Long semesterId,
            @Param("subjectId") Long subjectId,
            @Param("departmentId") Long departmentId,
            @Param("search") String search,
            @Param("hasSlot") Boolean hasSlot,
            @Param("notEnrolled") Boolean notEnrolled,
            @Param("studentId") Long studentId
    );

}
