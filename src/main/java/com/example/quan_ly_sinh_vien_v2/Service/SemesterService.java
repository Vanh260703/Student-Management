package com.example.quan_ly_sinh_vien_v2.Service;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.Semester.CreateSemesterRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Request.Semester.UpdateSemesterRequest;
import com.example.quan_ly_sinh_vien_v2.Exception.AlreadyExistsException;
import com.example.quan_ly_sinh_vien_v2.Exception.CreateFailException;
import com.example.quan_ly_sinh_vien_v2.Exception.NotFoundException;
import com.example.quan_ly_sinh_vien_v2.Exception.UpdateFailException;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.AcademicYear;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Semester;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.SemesterName;
import com.example.quan_ly_sinh_vien_v2.Repository.AcademicYearRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.SemesterRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class SemesterService {
    private final SemesterRepository semesterRepository;
    private final AcademicYearRepository academicYearRepository;

    public SemesterService(SemesterRepository semesterRepository, AcademicYearRepository academicYearRepository) {
        this.semesterRepository = semesterRepository;
        this.academicYearRepository = academicYearRepository;
    }

    // Get semesters
    public List<Semester> getSemesters(Long academicYearId, Boolean isActive, Integer semesterNumber) {
        return semesterRepository.searchSemesters(
                academicYearId,
                isActive,
                semesterNumber
        );
    }

    // Create semester
    public Semester createSemester(@Valid CreateSemesterRequest request) {
        if (request.getSemesterNumber() < 1 || request.getSemesterNumber() > 3) {
            throw new CreateFailException("Semester number invalid!");
        }

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new CreateFailException("Start date must be before end date!");
        }

        if (request.getRegistrationStart() != null &&
                request.getRegistrationEnd() != null) {

            if (request.getRegistrationStart().isAfter(request.getRegistrationEnd())) {
                throw new CreateFailException("Registration start must be before registration end!");
            }
        }

        AcademicYear academicYear = academicYearRepository.findById(request.getAcademicYearId())
                .orElseThrow(() -> new NotFoundException("Academic not found!"));

        SemesterName semesterName = SemesterName.valueOf("HK" + request.getSemesterNumber());

        if (semesterRepository.existsSemesterByAcademicYearIdAndSemesterNumber(request.getAcademicYearId(), request.getSemesterNumber())) {
            throw new AlreadyExistsException("Semester already exists!");
        }

        Semester semester = new Semester();

        semester.setAcademicYear(academicYear);
        semester.setSemesterNumber(request.getSemesterNumber());
        semester.setName(semesterName);
        semester.setStartDate(request.getStartDate());
        semester.setEndDate(request.getEndDate());
        semester.setIsActive(request.getIsActive());
        semester.setRegistrationStart(request.getRegistrationStart());
        semester.setRegistrationEnd(request.getRegistrationEnd());

        semesterRepository.save(semester);

        return semester;
    }

    // Update semester
    @Transactional
    public Semester updateSemester(Long semesterId, @Valid UpdateSemesterRequest request) {
        Semester semester = semesterRepository.findById(semesterId).orElseThrow(() -> new NotFoundException("Semester not found!"));

        if (request.getStartDate() == null && request.getEndDate() == null && request.getRegistrationStart() == null && request.getRegistrationEnd() == null) {
            throw new UpdateFailException("Update semester fail");
        }

        LocalDate newStartDate = request.getStartDate() != null
                ? request.getStartDate()
                : semester.getStartDate();

        LocalDate newEndDate = request.getEndDate() != null
                ? request.getEndDate()
                : semester.getEndDate();

        Instant newRegistrationStart = request.getRegistrationStart() != null
                ? request.getRegistrationStart()
                : semester.getRegistrationStart();

        Instant newRegistrationEnd = request.getRegistrationEnd() != null
                ? request.getRegistrationEnd()
                : semester.getRegistrationEnd();

        if (newStartDate.isAfter(newEndDate)) {
            throw new UpdateFailException("Start date must be before end date!");
        }

        if (newRegistrationStart != null && newRegistrationEnd != null) {

            if (newRegistrationStart.isAfter(newRegistrationEnd)) {
                throw new UpdateFailException("Registration start must be before registration end!");
            }

            Instant semesterStartInstant = newStartDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
            Instant semesterEndInstant = newEndDate.atTime(23, 59, 59)
                    .atZone(ZoneId.systemDefault())
                    .toInstant();

            if (newRegistrationStart.isBefore(semesterStartInstant)
                    || newRegistrationEnd.isAfter(semesterEndInstant)) {
                throw new UpdateFailException("Registration time must be within semester duration!");
            }
        }

        semester.setStartDate(newStartDate);
        semester.setEndDate(newEndDate);
        semester.setRegistrationStart(newRegistrationStart);
        semester.setRegistrationEnd(newRegistrationEnd);

        return semesterRepository.save(semester);
    }

    // Toggle semester status
    @Transactional
    public void toggleActive(Long semesterId) {
        Semester semester = semesterRepository.findById(semesterId).orElseThrow(() -> new NotFoundException("Semester not found!"));

        semester.setIsActive(!semester.getIsActive());
    }
}
