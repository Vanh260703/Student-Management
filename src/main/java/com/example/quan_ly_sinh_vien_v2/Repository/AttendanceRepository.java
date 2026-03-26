package com.example.quan_ly_sinh_vien_v2.Repository;

import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findAttendanceByEnrollmentIdAndDate(Long id, LocalDate date);


    List<Attendance> findAllByClassEntityIdAndDate(Long classId, LocalDate date);

    Optional<Attendance> findByIdAndDate(Long attendanceId, LocalDate date);
}
