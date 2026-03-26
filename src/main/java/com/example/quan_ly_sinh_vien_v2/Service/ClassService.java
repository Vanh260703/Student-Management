package com.example.quan_ly_sinh_vien_v2.Service;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.Class.CreateClassRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Request.Class.CreateClassScheduleRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Request.Class.UpdateStatusClassRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.ClassResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.ClassScheduleResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.StudentResponse;
import com.example.quan_ly_sinh_vien_v2.Exception.AlreadyExistsException;
import com.example.quan_ly_sinh_vien_v2.Exception.CreateFailException;
import com.example.quan_ly_sinh_vien_v2.Exception.NotFoundException;
import com.example.quan_ly_sinh_vien_v2.Exception.UpdateFailException;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.*;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.ClassStatus;
import com.example.quan_ly_sinh_vien_v2.Repository.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClassService {
    private final ClassEntityRepository classEntityRepository;
    private final SemesterRepository semesterRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherProfileRepository teacherProfileRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ClassScheduleRepository classScheduleRepository;

    public ClassService(ClassEntityRepository classEntityRepository, SemesterRepository semesterRepository, SubjectRepository subjectRepository, TeacherProfileRepository teacherProfileRepository, EnrollmentRepository enrollmentRepository, ClassScheduleRepository classScheduleRepository) {
        this.classEntityRepository = classEntityRepository;
        this.semesterRepository = semesterRepository;
        this.subjectRepository = subjectRepository;
        this.teacherProfileRepository = teacherProfileRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.classScheduleRepository = classScheduleRepository;
    }

    // Get classes
    public List<ClassResponse> getClasses(Long semesterId, Long subjectId, Long teacherId, ClassStatus status, String search, Boolean hasSlot) {
        String keyword = null;

        if (search != null && !search.trim().isEmpty()) {
            keyword = search.trim().toLowerCase();
        }

        List<ClassEntity> classes = classEntityRepository.searchClasses(
                semesterId,
                subjectId,
                teacherId,
                status,
                keyword,
                hasSlot
        );

        return classes.stream().map(ClassResponse::from).toList();
    }

    // Create class
    public ClassEntity createClass(@Valid CreateClassRequest request) {
        if (request.getSemesterId() ==  null && request.getSubjectId() == null && request.getTeacherId() == null && request.getClassCode() == null) {
            throw new CreateFailException("Create class fail!");
        }

        Semester semester = semesterRepository.findById(request.getSemesterId())
                .orElseThrow(() -> new NotFoundException("Semester not found!"));

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new NotFoundException("Subject not found!"));

        TeacherProfile teacher = teacherProfileRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new NotFoundException("Teacher not found!"));

        if (teacher.getDepartment().getId() != subject.getDepartment().getId()) {
            throw new CreateFailException("Create class fail!");
        }

        if (classEntityRepository.existsClassEntitiesByClassCode(request.getClassCode())) {
            throw new AlreadyExistsException("Class already exists!");
        }

        if (!semester.getIsActive()) {
            throw new CreateFailException("Semester invalid!");
        }

        ClassEntity classEntity = new ClassEntity();

        classEntity.setSemester(semester);
        classEntity.setSubject(subject);
        classEntity.setTeacher(teacher);
        classEntity.setClassCode(request.getClassCode());
        classEntity.setMaxStudents(request.getMaxStudents());
        classEntity.setRoom(request.getRoom());
        classEntity.setCurrentStudents(0);

        classEntityRepository.save(classEntity);

        return classEntity;
    }

    // Get details class
    public ClassResponse getDetailsClass(Long classId) {
        ClassEntity classEntity = classEntityRepository.findById(classId)
                .orElseThrow(() -> new NotFoundException("Class not found!"));

        return ClassResponse.from(classEntity);
    }

    // Update status class
    @Transactional
    public void updateStatus(Long classId, @Valid UpdateStatusClassRequest request) {
        ClassEntity classEntity = classEntityRepository.findById(classId)
                .orElseThrow(() -> new NotFoundException("Class not found!"));

        if (request.getStatus() == null) {
            throw new UpdateFailException("Update status class fail!");
        }

        if (classEntity.getStatus() == request.getStatus()) {
            throw new UpdateFailException("Update status class fail!");
        }

        classEntity.setStatus(request.getStatus());
    }

    // Delete class
    public void deleteClass(Long classId) {
        if (classEntityRepository.existsById(classId)) {
            throw new NotFoundException("Class not found!");
        }

        classEntityRepository.deleteById(classId);
    }

    // Get students in class
    public List<StudentResponse> getStudentsInClass(Long classId, String search) {
        if (!classEntityRepository.existsById(classId)) {
            throw new NotFoundException("Class not found!");
        }

        String keyword = null;

        if (search != null && !search.trim().isEmpty()) {
            keyword = search.trim().toLowerCase();
        }


        List<StudentProfile> students = enrollmentRepository.findStudentsByClassId(classId, keyword);

        return students.stream()
                .map(StudentResponse::from)
                .toList();
    }

    public ClassScheduleResponse createSchedule(Long classId, @Valid CreateClassScheduleRequest request) {
        ClassEntity classEntity = classEntityRepository.findById(classId)
                .orElseThrow(() -> new NotFoundException("Class not found!"));

        // ===== Lấy schedule đã tồn tại (nếu có) =====
        ClassSchedule existingSchedule = classScheduleRepository.findFirstByClassEntity(classEntity).orElse(null);

        LocalDate existingStartWeek = null;
        LocalDate existingEndWeek = null;

        if (existingSchedule != null) {
            existingStartWeek = existingSchedule.getWeekStart();
            existingEndWeek = existingSchedule.getWeekEnd();
        }

        List<ClassSchedule> schedulesToSave = new ArrayList<>();

        for (CreateClassScheduleRequest.ScheduleItem item : request.getSchedules()) {

            if (item.getDayOfWeek() < 0 || item.getDayOfWeek() > 6) {
                throw new CreateFailException("Invalid dayOfWeek");
            }

            if (item.getStartPeriod() > item.getEndPeriod()) {
                throw new CreateFailException("startPeriod must be <= endPeriod");
            }

            if (item.getStartWeek().isAfter(item.getEndWeek())) {
                throw new CreateFailException("startWeek must be before endWeek");
            }

            if (existingStartWeek != null) {
                if (!item.getStartWeek().equals(existingStartWeek) ||
                        !item.getEndWeek().equals(existingEndWeek)) {

                    throw new CreateFailException(
                            "All schedules of a class must have the same startWeek and endWeek"
                    );
                }
            }

            boolean overlap = classScheduleRepository.existsOverlappingSchedule(
                    classEntity,
                    item.getDayOfWeek(),
                    item.getStartPeriod(),
                    item.getEndPeriod()
            );

            if (overlap) {
                throw new CreateFailException(
                        "Schedule conflict at day " + item.getDayOfWeek() +
                                " (period " + item.getStartPeriod() + "-" + item.getEndPeriod() + ")"
                );
            }

            ClassSchedule schedule = new ClassSchedule();
            schedule.setClassEntity(classEntity);
            schedule.setDayOfWeek(item.getDayOfWeek());
            schedule.setStartPeriod(item.getStartPeriod());
            schedule.setEndPeriod(item.getEndPeriod());
            schedule.setRoom(item.getRoom());
            schedule.setWeekStart(item.getStartWeek());
            schedule.setWeekEnd(item.getEndWeek());

            schedulesToSave.add(schedule);
        }

        List<ClassSchedule> saved = classScheduleRepository.saveAll(schedulesToSave);

        List<ClassScheduleResponse.ScheduleInfo> responseList = saved.stream().map(s -> {
            ClassScheduleResponse.ScheduleInfo info = new ClassScheduleResponse.ScheduleInfo();
            info.setDayOfWeek(s.getDayOfWeek());
            info.setStartPeriod(s.getStartPeriod());
            info.setEndPeriod(s.getEndPeriod());
            info.setRoom(s.getRoom());
            info.setStartWeek(s.getWeekStart());
            info.setEndWeek(s.getWeekEnd());
            return info;
        }).toList();

        ClassScheduleResponse response = new ClassScheduleResponse();
        response.setSchedules(responseList);

        return response;
    }

    public ClassScheduleResponse getSchedules(Long classId) {
        ClassEntity classEntity = classEntityRepository.findById(classId)
                .orElseThrow(() -> new NotFoundException("Class not found!"));

        List<ClassSchedule> schedules = classScheduleRepository.findAllByClassEntity(classEntity);

        List<ClassScheduleResponse.ScheduleInfo> responseList = new ArrayList<>();

        for (ClassSchedule s : schedules) {
            ClassScheduleResponse.ScheduleInfo info = new ClassScheduleResponse.ScheduleInfo();

            info.setDayOfWeek(s.getDayOfWeek());
            info.setStartPeriod(s.getStartPeriod());
            info.setEndPeriod(s.getEndPeriod());
            info.setRoom(s.getRoom());
            info.setStartWeek(s.getWeekStart());
            info.setEndWeek(s.getWeekEnd());

            responseList.add(info);
        }

        ClassScheduleResponse response = new ClassScheduleResponse();
        response.setSchedules(responseList);

        return response;
    }
}
