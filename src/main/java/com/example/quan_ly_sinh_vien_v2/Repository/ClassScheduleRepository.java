package com.example.quan_ly_sinh_vien_v2.Repository;

import com.example.quan_ly_sinh_vien_v2.Modal.Entity.ClassEntity;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.ClassSchedule;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, Long> {
    @Query("""
    SELECT COUNT(cs) > 0
    FROM ClassSchedule cs
    WHERE cs.classEntity = :classEntity
      AND cs.dayOfWeek = :dayOfWeek
      AND cs.startPeriod <= :endPeriod
      AND cs.endPeriod >= :startPeriod
""")
    boolean existsOverlappingSchedule(
            @Param("classEntity") ClassEntity classEntity,
            @Param("dayOfWeek") Integer dayOfWeek,
            @Param("startPeriod") Integer startPeriod,
            @Param("endPeriod") Integer endPeriod
    );

    Optional<ClassSchedule> findFirstByClassEntity(ClassEntity classEntity);

    List<ClassSchedule> findAllByClassEntity(ClassEntity classEntity);

    List<ClassSchedule> findByClassEntityIn(List<ClassEntity> classes);
}
