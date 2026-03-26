package com.example.quan_ly_sinh_vien_v2.Service;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.Teacher.*;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.Attendance.AttendanceResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.ClassResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.Attendance.StudentAttendanceResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.Grade.ClassGradesResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.Grade.StudentGradeResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.GradeComponentResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.TeacherProfileResponse;
import com.example.quan_ly_sinh_vien_v2.Exception.AlreadyExistsException;
import com.example.quan_ly_sinh_vien_v2.Exception.CreateFailException;
import com.example.quan_ly_sinh_vien_v2.Exception.NotFoundException;
import com.example.quan_ly_sinh_vien_v2.Exception.UpdateFailException;
import com.example.quan_ly_sinh_vien_v2.Helper;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.*;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.Gender;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.GradeComponentType;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.LetterGrade;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.Role;
import com.example.quan_ly_sinh_vien_v2.Repository.*;
import com.example.quan_ly_sinh_vien_v2.Util.GenerateCode;
import com.example.quan_ly_sinh_vien_v2.Util.GenerateSchoolEmail;
import com.example.quan_ly_sinh_vien_v2.Util.PasswordGenerater;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.sql.Update;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TeacherService {
    private final ClassEntityRepository classEntityRepository;
    private final TeacherProfileRepository teacherProfileRepository;
    private final AttendanceRepository attendanceRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final GradeComponentRepository gradeComponentRepository;
    private final GradeRepository gradeRepository;
    private final EnrollmentService enrollmentService;

    public TeacherService(ClassEntityRepository classEntityRepository, TeacherProfileRepository teacherProfileRepository, AttendanceRepository attendanceRepository, EnrollmentRepository enrollmentRepository, UserRepository userRepository, GradeComponentRepository gradeComponentRepository, GradeRepository gradeRepository, EnrollmentService enrollmentService) {
        this.classEntityRepository = classEntityRepository;
        this.teacherProfileRepository = teacherProfileRepository;
        this.attendanceRepository = attendanceRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.gradeComponentRepository = gradeComponentRepository;
        this.gradeRepository = gradeRepository;
        this.enrollmentService = enrollmentService;
    }

    // Update class
    @Transactional
    public ClassResponse updateClass(String username, Long classId, @Valid UpdateClassRequest request) {
        ClassEntity classEntity = classEntityRepository.findById(classId)
                .orElseThrow(() -> new NotFoundException("Class not found!"));

        TeacherProfile teacher = teacherProfileRepository.findTeacherProfileByEmail(username)
                .orElseThrow(() -> new NotFoundException("Teacher not found!"));

        if (classEntity.getTeacher() != teacher) {
            throw new AuthorizationDeniedException("You don't have permission to access!");
        }

        if (request.getRoom() != null) {
            classEntity.setRoom(request.getRoom());
        }

        return ClassResponse.from(classEntity);
    }

    // Get classes
    public List<ClassResponse> getClasses(String username) {
        TeacherProfile teacher = teacherProfileRepository.findTeacherProfileByEmail(username)
                .orElseThrow(() -> new NotFoundException("Teacher not found!"));

        List<ClassEntity> classes = classEntityRepository.findAllByTeacher(teacher);

        return classes.stream().map(ClassResponse::from).toList();
    }

    // Get attendance in class
    public AttendanceResponse getAttendance(String username, Long classId, LocalDate date) {
        ClassEntity classEntity = classEntityRepository.findById(classId)
                .orElseThrow(() -> new NotFoundException("Class not found!"));

        TeacherProfile teacher = teacherProfileRepository.findTeacherProfileByEmail(username)
                .orElseThrow(() -> new NotFoundException("Teacher not found!"));

        if (classEntity.getTeacher() != teacher) {
            throw new AuthorizationDeniedException("You don't have permission to access!");
        }

        List<Enrollment> enrollments = enrollmentRepository.findAllByClassEntityId(classId);

        List<Attendance> attendances = attendanceRepository
                .findAllByClassEntityIdAndDate(classId, date);


        Map<Long, Attendance> attendanceMap = attendances.stream()
                .collect(Collectors.toMap(a -> a.getEnrollment().getId(), a -> a));

        List<StudentAttendanceResponse> students = new ArrayList<>();

        for (Enrollment enrollment : enrollments) {

            StudentProfile student = enrollment.getStudent();

            Attendance attendance = attendanceMap.get(enrollment.getId());

            StudentAttendanceResponse dto = new StudentAttendanceResponse();

            dto.setEnrollmentId(enrollment.getId());
            dto.setStudentCode(student.getStudentCode());
            dto.setName(student.getUser().getFullName());

            if (attendance != null) {
                dto.setStatus(attendance.getStatus());
            }

            students.add(dto);
        }

        AttendanceResponse response = new AttendanceResponse();
        response.setClassId(classId);
        response.setDate(date);
        response.setStudents(students);

        return response;
    }

    // Attendance many record
    public AttendanceResponse attendanceStudents(String username, Long classId, LocalDate date, @Valid List<UpdateStatusAttendanceRequest> requests) {
        ClassEntity classEntity = classEntityRepository.findById(classId)
                .orElseThrow(() -> new NotFoundException("Class not found!"));

        TeacherProfile teacher = teacherProfileRepository.findTeacherProfileByEmail(username)
                .orElseThrow(() -> new NotFoundException("Teacher not found!"));

        if (classEntity.getTeacher() != teacher) {
            throw new AuthorizationDeniedException("You don't have permission to access!");
        }

        List<Enrollment> enrollments = enrollmentRepository.findAllByClassEntityId(classId);

        Map<Long, Enrollment> enrollmentMap = enrollments.stream()
                .collect(Collectors.toMap(Enrollment::getId, e -> e));

        List<Attendance> attendances = attendanceRepository.findAllByClassEntityIdAndDate(classId, date);

        Map<Long, Attendance> attendanceMap = attendances.stream()
                .collect(Collectors.toMap(a -> a.getEnrollment().getId(), a -> a));

        List<Attendance> saveList = new ArrayList<>();


        for (UpdateStatusAttendanceRequest request : requests) {
            Enrollment enrollment = enrollmentMap.get(request.getEnrollmentId());

            if (enrollment == null) {
                throw new NotFoundException("Enrollment not found!");
            }

            Attendance attendance = attendanceMap.get(request.getEnrollmentId());

            if (attendance == null) {
                attendance = new Attendance();
                attendance.setEnrollment(enrollment);
                attendance.setClassEntity(classEntity);
                attendance.setDate(date);
            }

            attendance.setStatus(request.getStatus());
            attendance.setNotedBy(teacher.getUser());

            saveList.add(attendance);
        }

        attendanceRepository.saveAll(saveList);

        return getAttendance(username, classId, date);
    }

    @Transactional
    public void attendanceStudent(String username, Long classId, Long attendanceId, LocalDate date, @Valid UpdateStatusAttendanceRequest request) {
        ClassEntity classEntity = classEntityRepository.findById(classId)
                .orElseThrow(() -> new NotFoundException("Class not found!"));

        TeacherProfile teacher = teacherProfileRepository.findTeacherProfileByEmail(username)
                .orElseThrow(() -> new NotFoundException("Teacher not found!"));

        if (classEntity.getTeacher() != teacher) {
            throw new AuthorizationDeniedException("You don't have permission to access!");
        }

        Attendance attendance = attendanceRepository.findByIdAndDate(attendanceId, date)
                .orElseThrow(() -> new NotFoundException("Attendance not found!"));

        attendance.setStatus(request.getStatus());
    }

    public List<GradeComponent> gradeComponents(String username, Long classId) {
        ClassEntity classEntity = classEntityRepository.findById(classId)
                .orElseThrow(() -> new NotFoundException("Class not found!"));

        User user = userRepository.findByEmail(username).orElseThrow(() -> new NotFoundException("User not found!"));

        if (user.getRole() == Role.ROLE_TEACHER) {
            TeacherProfile teacher = teacherProfileRepository.findTeacherProfileByEmail(username)
                    .orElseThrow(() -> new NotFoundException("Teacher not found!"));

            if (classEntity.getTeacher() != teacher) {
                throw new AuthorizationDeniedException("You don't have permission to access!");
            }
        }

        return gradeComponentRepository.findAllByClassEntityId(classId);
    }

    public GradeComponentResponse createGradeComponent(String username, Long classId, @Valid CreateGradeComponentRequest request) {
        ClassEntity classEntity = classEntityRepository.findById(classId)
                .orElseThrow(() -> new NotFoundException("Class not found!"));

        User user = userRepository.findByEmail(username).orElseThrow(() -> new NotFoundException("User not found!"));

        if (user.getRole() == Role.ROLE_TEACHER) {
            TeacherProfile teacher = teacherProfileRepository.findTeacherProfileByEmail(username)
                    .orElseThrow(() -> new NotFoundException("Teacher not found!"));

            if (classEntity.getTeacher() != teacher) {
                throw new AuthorizationDeniedException("You don't have permission to access!");
            }
        }

        if (gradeComponentRepository.existsGradeComponentByClassEntityIdAndType(classId, request.getType())) {
            throw new AlreadyExistsException("Grade component is already in class!");
        }

        List<GradeComponent> gradeComponents = gradeComponentRepository.findAllByClassEntityId(classId);

        int totalWeight = 0;

        for (GradeComponent gradeComponent : gradeComponents) {
            totalWeight += gradeComponent.getWeight();
        }

        if (totalWeight + request.getWeight() > 100) {
            throw new CreateFailException("Total weight of grade components cannot exceed 100%!");
        }

        String name = null;

        switch (request.getType()) {
            case ATTENDANCE -> name = "Quá trình";
            case ASSIGNMENT -> name = "Bài tập";
            case MIDTERM -> name = "Giữa kỳ";
            case FINAL -> name = "Cuối kỳ";
        }

        GradeComponent gradeComponent = new GradeComponent();

        gradeComponent.setClassEntity(classEntity);
        gradeComponent.setName(name);
        gradeComponent.setType(request.getType());
        gradeComponent.setWeight(request.getWeight());
        gradeComponent.setMaxScore(request.getMaxScore());

        gradeComponentRepository.save(gradeComponent);

        GradeComponentResponse response = new GradeComponentResponse();

        response.setClassId(classId);
        response.setType(gradeComponent.getType());
        response.setWeight(gradeComponent.getWeight());
        response.setName(gradeComponent.getName());

        return response;
    }

    @Transactional
    public GradeComponentResponse updateGradeComponent(String username, Long classId, Long gradeComponentId, @Valid UpdateGradeComponentRequest request) {
        ClassEntity classEntity = classEntityRepository.findById(classId)
                .orElseThrow(() -> new NotFoundException("Class not found!"));

        TeacherProfile teacher = teacherProfileRepository.findTeacherProfileByEmail(username)
                .orElseThrow(() -> new NotFoundException("Teacher not found!"));

        if (classEntity.getTeacher() != teacher) {
            throw new AuthorizationDeniedException("You don't have permission to access!");
        }

        GradeComponent gradeComponent =
                gradeComponentRepository
                        .findByIdAndClassEntityId(gradeComponentId, classId)
                        .orElseThrow(() -> new NotFoundException("Grade component not found!"));


        if (request.getWeight() == null && request.getMaxScore() == null) {
            throw new UpdateFailException("Nothing to update");
        }

        if (request.getWeight() != null) {

            int totalWeight = gradeComponentRepository.sumWeightByClassId(classId);

            int newTotal = totalWeight - gradeComponent.getWeight() + request.getWeight();

            if (newTotal > 100) {
                throw new UpdateFailException("Total weight cannot exceed 100%");
            }

            gradeComponent.setWeight(request.getWeight());
        }

        if (request.getMaxScore() != null) {
            gradeComponent.setMaxScore(request.getMaxScore());
        }

        gradeComponentRepository.save(gradeComponent);

        GradeComponentResponse response = new GradeComponentResponse();

        response.setClassId(classId);
        response.setType(gradeComponent.getType());
        response.setWeight(gradeComponent.getWeight());
        response.setName(gradeComponent.getName());

        return response;
     }

    @Transactional
    public void deleteGradeComponent(String username, Long classId, Long gradeComponentId) {
        ClassEntity classEntity = classEntityRepository.findById(classId)
                .orElseThrow(() -> new NotFoundException("Class not found!"));

        TeacherProfile teacher = teacherProfileRepository.findTeacherProfileByEmail(username)
                .orElseThrow(() -> new NotFoundException("Teacher not found!"));

        if (classEntity.getTeacher() != teacher) {
            throw new AuthorizationDeniedException("You don't have permission to access!");
        }

        GradeComponent gradeComponent =
                gradeComponentRepository
                        .findByIdAndClassEntityId(gradeComponentId, classId)
                        .orElseThrow(() -> new NotFoundException("Grade component not found!"));

        gradeComponentRepository.delete(gradeComponent);
    }

    public ClassGradesResponse getGradesInClass(String username, Long classId, Long componentId, Boolean isPublished, String search) {
        ClassEntity classEntity = classEntityRepository.findById(classId)
                .orElseThrow(() -> new NotFoundException("Class not found!"));

        User user = userRepository.findByEmail(username).orElseThrow(() -> new NotFoundException("User not found!"));

        if (user.getRole() == Role.ROLE_TEACHER) {
            TeacherProfile teacher = teacherProfileRepository.findTeacherProfileByEmail(username)
                    .orElseThrow(() -> new NotFoundException("Teacher not found!"));

            if (classEntity.getTeacher() != teacher) {
                throw new AuthorizationDeniedException("You don't have permission to access!");
            }
        }

        String keyword = null;

        if (search != null && !search.trim().isEmpty()) {
            keyword = search.trim().toLowerCase();
        }

       List<Grade> grades = gradeRepository.findAllByClassId(classId, componentId);

        List<Enrollment> enrollments = enrollmentRepository.findAllByClassEntityId(classId);

        Map<Long, Map<GradeComponentType, Double>> gradeMap = new HashMap<>();

        for (Grade g : grades) {
            Long enrollmentId = g.getEnrollment().getId();

            gradeMap
                    .computeIfAbsent(enrollmentId, k -> new EnumMap<>(GradeComponentType.class))
                    .put(
                            g.getGradeComponent().getType(),
                            g.getScore()
                    );
        }

        List<StudentGradeResponse> students = new ArrayList<>();

        for (Enrollment e : enrollments) {

            StudentProfile student = e.getStudent();

            Map<GradeComponentType, Double> gradesOfStudent =
                    gradeMap.getOrDefault(e.getId(), new EnumMap<>(GradeComponentType.class));

            StudentGradeResponse response = new StudentGradeResponse();

            response.setEnrollmentId(e.getId());
            response.setStudentCode(student.getStudentCode());
            response.setName(student.getUser().getFullName());
            response.setGrades(gradesOfStudent);

            students.add(response);
        }

        ClassGradesResponse response = new ClassGradesResponse();
        response.setClassId(classId);
        response.setStudents(students);

        return response;
    }

    public StudentGradeResponse createGrade(String username, Long classId, @Valid CreateGradeRequest request) {
        ClassEntity classEntity = classEntityRepository.findById(classId)
                .orElseThrow(() -> new NotFoundException("Class not found!"));

        TeacherProfile teacher = teacherProfileRepository.findTeacherProfileByEmail(username)
                .orElseThrow(() -> new NotFoundException("Teacher not found!"));

        if (!classEntity.getTeacher().equals(teacher)) {
            throw new AuthorizationDeniedException("You don't have permission to access!");
        }


        Enrollment enrollment = enrollmentRepository.findById(request.getEnrollmentId())
                .orElseThrow(() -> new NotFoundException("Enrollment not found!"));

        GradeComponent gradeComponent = gradeComponentRepository.findById(request.getComponentId())
                .orElseThrow(() -> new NotFoundException("Grade component not found!"));

        if (!enrollment.getClassEntity().getId().equals(classId) ) {
            throw new NotFoundException("Student is not enrolled in this class!");
        }

        if (!gradeComponent.getClassEntity().getId().equals(classId)) {
            throw new NotFoundException("Grade component does not belong to this class!");
        }

        if (request.getScore().compareTo(gradeComponent.getMaxScore()) > 0) {
            throw new CreateFailException("Score exceeds maximum score!");
        }

        LetterGrade letterGrade = Helper.convertScoreToLetterGrade(request.getScore());

        if (gradeRepository.existsGradeByEnrollmentAndGradeComponent(enrollment, gradeComponent)) {
            throw new CreateFailException("Grade already exists!");
        }

        Grade grade = new Grade();

        grade.setEnrollment(enrollment);
        grade.setGradeComponent(gradeComponent);
        grade.setScore(request.getScore());
        grade.setLetterGrade(letterGrade);
        grade.setGradedBy(teacher);
        grade.setGradedAt(LocalDateTime.now());

        gradeRepository.save(grade);

        enrollmentService.recalculateFinalScore(enrollment);

        Map<GradeComponentType, Double> gradeMap = new HashMap<>();

        gradeMap.put(gradeComponent.getType(), request.getScore());

        StudentGradeResponse response = new StudentGradeResponse();

        response.setEnrollmentId(enrollment.getId());
        response.setStudentCode(enrollment.getStudent().getStudentCode());
        response.setName(enrollment.getStudent().getUser().getFullName());
        response.setGrades(gradeMap);

        return response;
    }

    // Update grade student
    @Transactional
    public StudentGradeResponse updateGrade(String username, Long classId, Long gradeId, @Valid UpdateGradeRequest request) {
        ClassEntity classEntity = classEntityRepository.findById(classId)
                .orElseThrow(() -> new NotFoundException("Class not found!"));

        TeacherProfile teacher = teacherProfileRepository.findTeacherProfileByEmail(username)
                .orElseThrow(() -> new NotFoundException("Teacher not found!"));

        if (!classEntity.getTeacher().equals(teacher)) {
            throw new AuthorizationDeniedException("You don't have permission to access!");
        }

        Grade grade = gradeRepository.findById(gradeId).orElseThrow(() -> new NotFoundException("Grade not found!"));

        if (!grade.getEnrollment().getClassEntity().equals(classEntity)) {
            throw new UpdateFailException("Grade does not belong to this class!");
        }

        if (request.getComponentId() == null || request.getScore() == null) {
            throw new UpdateFailException("Component ID and score must not be null!");
        }

        GradeComponent gradeComponent = gradeComponentRepository.findById(request.getComponentId())
                .orElseThrow(() -> new NotFoundException("Grade component not found!"));

        if (request.getScore() > gradeComponent.getMaxScore()) {
            throw new UpdateFailException("Score exceeds the maximum allowed for this component!");
        }

        if (!gradeComponent.getClassEntity().equals(classEntity)) {
            throw new UpdateFailException("Grade component does not belong to this class!");
        }

        LetterGrade letterGrade = Helper.convertScoreToLetterGrade(request.getScore());

        grade.setGradeComponent(gradeComponent);
        grade.setScore(request.getScore());
        grade.setGradedAt(LocalDateTime.now());
        grade.setLetterGrade(letterGrade);
        grade.setGradedBy(teacher);

        gradeRepository.save(grade);

        enrollmentService.recalculateFinalScore(grade.getEnrollment());

        StudentGradeResponse response = new StudentGradeResponse();

        Map<GradeComponentType, Double> gradeMap = new HashMap<>();

        gradeMap.put(gradeComponent.getType(), request.getScore());

        response.setEnrollmentId(grade.getEnrollment().getId());
        response.setStudentCode(grade.getEnrollment().getStudent().getStudentCode());
        response.setName(grade.getEnrollment().getStudent().getUser().getFullName());
        response.setGrades(gradeMap);

        return response;
    }

    // Import grades for class
    public List<StudentGradeResponse> importGrades(String username, Long classId, MultipartFile file) {
        ClassEntity classEntity = classEntityRepository.findById(classId)
                .orElseThrow(() -> new NotFoundException("Class not found!"));

        TeacherProfile teacher = teacherProfileRepository.findTeacherProfileByEmail(username)
                .orElseThrow(() -> new NotFoundException("Teacher not found!"));

        if (!classEntity.getTeacher().equals(teacher)) {
            throw new AuthorizationDeniedException("You don't have permission to access!");
        }

        List<Enrollment> enrollments = enrollmentRepository.findAllByClassEntityId(classId);
        List<GradeComponent> components = gradeComponentRepository.findAllByClassEntityId(classId);

        Map<String, Enrollment> enrollmentMap = enrollments.stream()
                .collect(Collectors.toMap(
                        e -> e.getStudent().getStudentCode(),
                        Function.identity()
                ));

        Map<GradeComponentType, GradeComponent> componentMap = components.stream()
                .collect(Collectors.toMap(
                        GradeComponent::getType,
                        Function.identity()
                ));

        List<Grade> gradesToSave = new ArrayList<>();
        List<StudentGradeResponse> responses = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            if (headerRow == null) {
                throw new CreateFailException("Invalid file: missing header row");
            }

            Map<Integer, GradeComponentType> columnMap = new HashMap<>();

            for (Cell cell : headerRow) {
                String header = cell.getStringCellValue().trim();

                if (header.equalsIgnoreCase("studentCode")) continue;

                try {
                    GradeComponentType type = GradeComponentType.valueOf(header.toUpperCase());
                    columnMap.put(cell.getColumnIndex(), type);
                } catch (IllegalArgumentException e) {
                    throw new CreateFailException("Invalid column: " + header);
                }
            }

            if (columnMap.isEmpty()) {
                throw new CreateFailException("No grade component columns found!");
            }

            for (Row row : sheet) {

                if (row.getRowNum() == 0) continue;

                Cell firstCell = row.getCell(0);
                if (firstCell == null || firstCell.getCellType() == CellType.BLANK) {
                    continue;
                }

                String studentCode = Helper.getCellString(row, 0);

                Enrollment enrollment = enrollmentMap.get(studentCode);

                if (enrollment == null) {
                    throw new NotFoundException("Student not found: " + studentCode);
                }

                Map<GradeComponentType, Double> gradeMap = new EnumMap<>(GradeComponentType.class);

                for (Map.Entry<Integer, GradeComponentType> entry : columnMap.entrySet()) {

                    Integer colIndex = entry.getKey();
                    GradeComponentType type = entry.getValue();

                    String value = Helper.getCellString(row, colIndex);

                    if (value == null || value.isBlank()) continue;

                    Double score = Double.parseDouble(value);

                    GradeComponent component = componentMap.get(type);

                    if (component == null) {
                        throw new NotFoundException("Component not found in class: " + type);
                    }

                    // validate max score
                    if (score.compareTo(component.getMaxScore()) > 0) {
                        throw new CreateFailException(
                                "Score exceeds max score for " + type + " - student: " + studentCode
                        );
                    }

                    Grade grade = gradeRepository
                            .findByEnrollmentAndGradeComponent(enrollment, component)
                            .orElse(new Grade());

                    grade.setEnrollment(enrollment);
                    grade.setGradeComponent(component);
                    grade.setScore(score);
                    grade.setLetterGrade(Helper.convertScoreToLetterGrade(score));
                    grade.setGradedBy(teacher);
                    grade.setGradedAt(LocalDateTime.now());

                    gradesToSave.add(grade);
                    gradeMap.put(type, score);
                }

                StudentGradeResponse res = new StudentGradeResponse();
                res.setEnrollmentId(enrollment.getId());
                res.setStudentCode(studentCode);
                res.setName(enrollment.getStudent().getUser().getFullName());
                res.setGrades(gradeMap);

                responses.add(res);
            }

            gradeRepository.saveAll(gradesToSave);

            for (Grade g : gradesToSave) {
                enrollmentService.recalculateFinalScore(g.getEnrollment());
            }

        } catch (Exception e) {
            throw new CreateFailException("Import failed: " + e.getMessage());
        }

        return responses;
    }

    // Publish grades
    @Transactional
    public Integer publishGrades(String username, Long classId) {
        ClassEntity classEntity = classEntityRepository.findById(classId)
                .orElseThrow(() -> new NotFoundException("Class not found!"));

        TeacherProfile teacher = teacherProfileRepository.findTeacherProfileByEmail(username)
                .orElseThrow(() -> new NotFoundException("Teacher not found!"));

        if (!classEntity.getTeacher().equals(teacher)) {
            throw new AuthorizationDeniedException("You don't have permission to access!");
        }

        return gradeRepository.publishGrades(classId);
    }

    public TeacherProfileResponse getMyProfile(String username) {
        TeacherProfile teacher = teacherProfileRepository.findTeacherProfileByEmail(username)
                .orElseThrow(() -> new NotFoundException("Teacher not found!"));

        return TeacherProfileResponse.from(teacher);
    }

    @Transactional
    public TeacherProfileResponse updateProfile(String username, @Valid UpdateProfileRequest request) {
        TeacherProfile teacher = teacherProfileRepository.findTeacherProfileByEmail(username)
                .orElseThrow(() -> new NotFoundException("Teacher not found!"));

        User user = teacher.getUser();

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            String fullName = request.getFullName().trim();

            if (fullName.length() < 2) {
                throw new UpdateFailException("Full name must be at least 2 characters");
            }

            user.setFullName(fullName);
            user.setNormalizeName(Helper.normalizeFullName(fullName));
        }

        // Phone
        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            String phone = request.getPhone().trim();

            if (!phone.matches("^(0|\\+84)[0-9]{9}$")) {
                throw new UpdateFailException("Invalid phone number");
            }

            user.setPhone(phone);
        }

        // Personal email
        if (request.getPersonalEmail() != null && !request.getPersonalEmail().isBlank()) {
            String email = request.getPersonalEmail().trim();

            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                throw new UpdateFailException("Invalid email format");
            }

            if (userRepository.existsUserByPersonalEmail(email)) {
                throw new UpdateFailException("Email already in use");
            }

            user.setPersonalEmail(email);
        }

        if (request.getDayOfBirth() != null) {
            LocalDate dob = request.getDayOfBirth();

            if (dob.isAfter(LocalDate.now())) {
                throw new UpdateFailException("Date of birth cannot be in the future");
            }

            user.setDateOfBirth(dob);
        }

        if (request.getAddress() != null) {
            user.setAddress(request.getAddress().trim());
        }

        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }

        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        return TeacherProfileResponse.from(teacher);
    }
}
