package com.example.quan_ly_sinh_vien_v2.Repository;

import com.example.quan_ly_sinh_vien_v2.Modal.Entity.GradeComponent;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.GradeComponentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeComponentRepository extends JpaRepository<GradeComponent, Long> {

    List<GradeComponent> findAllByClassEntityId(Long classId);

    boolean existsGradeComponentByClassEntityIdAndType(Long classId, GradeComponentType type);

    Optional<GradeComponent> findByIdAndClassEntityId(Long gradeComponentId, Long classId);


    @Query("""
        SELECT SUM(gc.weight) FROM GradeComponent gc
        WHERE gc.classEntity.id = :classId
    """)
    Integer sumWeightByClassId(Long classId);
}
