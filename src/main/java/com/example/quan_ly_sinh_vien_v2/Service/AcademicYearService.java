package com.example.quan_ly_sinh_vien_v2.Service;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.AcademicYear.CreateAcademicYearRequest;
import com.example.quan_ly_sinh_vien_v2.Exception.AlreadyExistsException;
import com.example.quan_ly_sinh_vien_v2.Exception.CreateFailException;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.AcademicYear;
import com.example.quan_ly_sinh_vien_v2.Repository.AcademicYearRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AcademicYearService {
    private final AcademicYearRepository academicYearRepository;

    public AcademicYearService(AcademicYearRepository academicYearRepository) {
        this.academicYearRepository = academicYearRepository;
    }

    // Get academic years
    public List<AcademicYear> getAcademicYears(Boolean isCurrent) {
        if (isCurrent != null) {
            return academicYearRepository.findAcademicYearByIsCurrent(isCurrent);
        }

        return academicYearRepository.findAll();
    }

    public AcademicYear createAcademicYear(@Valid CreateAcademicYearRequest request) {
        if (academicYearRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new AlreadyExistsException("Academic year name already exists!");
        }

        if (academicYearRepository.existsByStartDateAndEndDate(
                request.getStartDate(),
                request.getEndDate()
        )) {
            throw new AlreadyExistsException("Academic year with same date range already exists!");
        }

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new CreateFailException("Start date must be before end date!");
        }

        AcademicYear academicYear = new AcademicYear();

        academicYear.setName(request.getName());
        academicYear.setStartDate(request.getStartDate());
        academicYear.setEndDate(request.getEndDate());
        academicYear.setIsCurrent(request.getIsCurrent());

        academicYearRepository.save(academicYear);

        return academicYear;
    }
}
