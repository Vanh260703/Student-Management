package com.example.quan_ly_sinh_vien_v2.Repository;

import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramRepository extends JpaRepository<Program, Long> {
    @Query("""
        SELECT t FROM Program t 
        WHERE (:departmentId IS NULL OR t.department.id = :departmentId)
        AND (
            :search IS NULL 
            OR t.normalizeName LIKE CONCAT('%', :search, '%')
            OR LOWER(t.code) LIKE LOWER(CONCAT('%', :search, '%')) 
            )
    """)
    List<Program> searchPrograms(
            @Param("departmentId") Long departmentId,
            @Param("search") String search
    );

    @Query("""
        SELECT COUNT(t) > 0 FROM Program t
        WHERE LOWER(t.code) LIKE LOWER(:code) 
    """)
    boolean existsProgramByCode(@Param("code") String code);

    @Query("""
        SELECT SUM(p.totalCredits) FROM Program p
        WHERE p.department.id = :departmentId
    """)
    Optional<Integer> sumCreditsByDepartmentId(@Param("departmentId") Long departmentId);
}
