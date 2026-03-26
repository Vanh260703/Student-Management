package com.example.quan_ly_sinh_vien_v2.Service;

import com.example.quan_ly_sinh_vien_v2.Config.MinioProperties;
import com.example.quan_ly_sinh_vien_v2.DTO.Request.Student.UpdateProfileRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.Grade.AllGradeStudent;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.Grade.StudentGradeResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.ProgramResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.StudentPaymentHistoryResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.StudentProfileResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.StudentTimetableResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.StudentTuitionResponse;
import com.example.quan_ly_sinh_vien_v2.Exception.NotFoundException;
import com.example.quan_ly_sinh_vien_v2.Exception.UpdateFailException;
import com.example.quan_ly_sinh_vien_v2.Helper;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.*;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.EnrollmentStatus;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.GradeComponentType;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.PaymentStatus;
import com.example.quan_ly_sinh_vien_v2.Repository.*;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private final StudentProfileRepository studentProfileRepository;
    private final SemesterRepository semesterRepository;
    private final ClassEntityRepository classEntityRepository;
    private final GradeRepository gradeRepository;
    private final ProgramRepository programRepository;
    private final ProgramSubjectRepository programSubjectRepository;
    private final SubjectRepository subjectRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final TuitionFeeRepository tuitionFeeRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final ClassScheduleRepository classScheduleRepository;
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public StudentService(StudentProfileRepository studentProfileRepository, SemesterRepository semesterRepository, ClassEntityRepository classEntityRepository, GradeRepository gradeRepository, ProgramRepository programRepository, ProgramSubjectRepository programSubjectRepository, SubjectRepository subjectRepository, EnrollmentRepository enrollmentRepository, TuitionFeeRepository tuitionFeeRepository, PaymentRepository paymentRepository, UserRepository userRepository, ClassScheduleRepository classScheduleRepository, MinioClient minioClient, MinioProperties minioProperties) {
        this.studentProfileRepository = studentProfileRepository;
        this.semesterRepository = semesterRepository;
        this.classEntityRepository = classEntityRepository;
        this.gradeRepository = gradeRepository;
        this.programRepository = programRepository;
        this.programSubjectRepository = programSubjectRepository;
        this.subjectRepository = subjectRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.tuitionFeeRepository = tuitionFeeRepository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.classScheduleRepository = classScheduleRepository;
        this.minioClient = minioClient;
        this.minioProperties = minioProperties;
    }

    public AllGradeStudent getAllGrades(String username) {
        StudentProfile student = studentProfileRepository.findStudentByEmail(username)
                .orElseThrow(() -> new NotFoundException("Student not found!"));

        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(student.getId());

        if (enrollments.isEmpty()) {
            throw new NotFoundException("No enrollments found!");
        }

        List<Long> enrollmentIds = enrollments.stream()
                .map(Enrollment::getId)
                .toList();

        List<Grade> grades = gradeRepository
                .findByEnrollmentIdInAndIsPublished(enrollmentIds, true);

        Map<Long, List<Grade>> gradeMap = grades.stream()
                .collect(Collectors.groupingBy(
                        g -> g.getEnrollment().getId()
                ));

        Map<Semester, List<Enrollment>> grouped =
                enrollments.stream()
                        .collect(Collectors.groupingBy(
                                e -> e.getClassEntity().getSemester()
                        ));

        AllGradeStudent response = new AllGradeStudent();

        AllGradeStudent.StudentInfo studentInfo = new AllGradeStudent.StudentInfo();
        studentInfo.setStudentCode(student.getStudentCode());
        studentInfo.setName(student.getUser().getFullName());

        response.setStudent(studentInfo);

        List<AllGradeStudent.SemesterInfo> semesterInfos = new ArrayList<>();

        for (Map.Entry<Semester, List<Enrollment>> entry : grouped.entrySet()) {

            Semester semester = entry.getKey();
            List<Enrollment> enrollmentList = entry.getValue();

            List<AllGradeStudent.CourseInfo> courseList = new ArrayList<>();

            for (Enrollment e : enrollmentList) {

                List<Grade> gradeList = gradeMap.getOrDefault(e.getId(), Collections.emptyList());

                if (gradeList.isEmpty()) continue;

                Map<GradeComponentType, Double> gradeDetail = new EnumMap<>(GradeComponentType.class);

                for (Grade g : gradeList) {
                    gradeDetail.put(
                            g.getGradeComponent().getType(),
                            g.getScore()
                    );
                }

                // build course info
                AllGradeStudent.CourseInfo courseInfo = getCourseInfo(e, gradeDetail);

                courseList.add(courseInfo);
            }

            if (courseList.isEmpty()) continue;

            AllGradeStudent.SemesterInfo semesterInfo = new AllGradeStudent.SemesterInfo();
            semesterInfo.setSemesterId(semester.getId());
            semesterInfo.setSemesterName(semester.getName());
            semesterInfo.setCourses(courseList);

            semesterInfos.add(semesterInfo);
        }

        response.setSemesters(semesterInfos);

        int totalCredits = programRepository.sumCreditsByDepartmentId(student.getDepartment().getId()).orElse(0);

        AllGradeStudent.SummaryInfo summaryInfo = new AllGradeStudent.SummaryInfo();

        summaryInfo.setGpa(student.getGpa());
        summaryInfo.setTotalCredits(totalCredits);
        summaryInfo.setPassedCredits(student.getAccumulatedCredits());

        response.setSummary(summaryInfo);

        return response;
    }

    private static AllGradeStudent.@NonNull CourseInfo getCourseInfo(Enrollment e, Map<GradeComponentType, Double> gradeDetail) {
        AllGradeStudent.CourseInfo courseInfo = new AllGradeStudent.CourseInfo();
        courseInfo.setClassId(e.getClassEntity().getId());
        courseInfo.setClassCode(e.getClassEntity().getClassCode());
        courseInfo.setSubjectName(e.getClassEntity().getSubject().getName());
        courseInfo.setCredits(e.getClassEntity().getSubject().getCredits());
        courseInfo.setGrades(gradeDetail);
        courseInfo.setFinalScore(e.getFinalScore());
        courseInfo.setFinalLetterGrade(e.getFinalLetterGrade());
        courseInfo.setIsPassed(e.getIsPassed());
        return courseInfo;
    }

    public StudentGradeResponse getGradesInClass(String username, Long classId) {
        StudentProfile student = studentProfileRepository.findStudentByEmail(username)
                .orElseThrow(() -> new NotFoundException("Student not found!"));

        ClassEntity classEntity = classEntityRepository.findById(classId)
                .orElseThrow(() -> new NotFoundException("Class not found!"));

        Enrollment enrollment = enrollmentRepository.findByStudentIdAndClassEntityId(student.getId(), classEntity.getId());

        List<Grade> grades = gradeRepository.findAllGradesByStudentId(student.getId());

        Map<GradeComponentType, Double> gradeMap = new HashMap<>();

        for (Grade g : grades) {
            gradeMap.put(g.getGradeComponent().getType(), g.getScore());
        }

        StudentGradeResponse response = new StudentGradeResponse();

        response.setEnrollmentId(enrollment.getId());
        response.setName(student.getUser().getFullName());
        response.setStudentCode(student.getStudentCode());
        response.setGrades(gradeMap);

        return response;
    }

    // Get profile
    public StudentProfileResponse getProfile(String username) {
        StudentProfile student = studentProfileRepository.findStudentByEmail(username)
                .orElseThrow(() -> new NotFoundException("Student not found!"));

        return StudentProfileResponse.from(student);
    }

    // Update profile
    @Transactional
    public StudentProfileResponse updateProfile(String username, @Valid UpdateProfileRequest request) {
        StudentProfile student = studentProfileRepository.findStudentByEmail(username)
                .orElseThrow(() -> new NotFoundException("Student not found!"));

        User user = student.getUser();

        // Full name
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

        return StudentProfileResponse.from(student);
    }

    @Transactional
    public String uploadAvatar(String username, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new UpdateFailException("Avatar file is required");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new UpdateFailException("Avatar must be an image");
        }

        StudentProfile student = studentProfileRepository.findStudentByEmail(username)
                .orElseThrow(() -> new NotFoundException("Student not found!"));

        String objectName = buildAvatarObjectName(student, file.getOriginalFilename());

        try (InputStream inputStream = file.getInputStream()) {
            ensureBucketExists();

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.getBucket())
                            .object(objectName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(contentType)
                            .build()
            );
        } catch (Exception ex) {
            throw new UpdateFailException("Upload avatar failed");
        }

        String avatarUrl = buildAvatarUrl(objectName);

        User user = student.getUser();
        user.setAvatarUrl(avatarUrl);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return avatarUrl;
    }

    private void ensureBucketExists() throws Exception {
        boolean bucketExists = minioClient.bucketExists(
                BucketExistsArgs.builder()
                        .bucket(minioProperties.getBucket())
                        .build()
        );

        if (!bucketExists) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(minioProperties.getBucket())
                            .build()
            );
        }
    }

    private String buildAvatarObjectName(StudentProfile student, String originalFilename) {
        String extension = "";

        if (originalFilename != null) {
            int lastDotIndex = originalFilename.lastIndexOf('.');
            if (lastDotIndex >= 0) {
                extension = originalFilename.substring(lastDotIndex).toLowerCase(Locale.ROOT);
            }
        }

        if (extension.isBlank()) {
            extension = ".jpg";
        }

        return "students/%s/%d%s".formatted(
                student.getStudentCode(),
                System.currentTimeMillis(),
                extension
        );
    }

    private String buildAvatarUrl(String objectName) {
        String baseUrl = minioProperties.getPublicUrl();

        if (baseUrl == null || baseUrl.isBlank()) {
            baseUrl = minioProperties.getUrl();
        }

        return "%s/%s/%s".formatted(
                removeTrailingSlash(baseUrl),
                minioProperties.getBucket(),
                objectName
        );
    }

    private String removeTrailingSlash(String value) {
        if (value.endsWith("/")) {
            return value.substring(0, value.length() - 1);
        }

        return value;
    }

    public ProgramResponse getProgramOfStudent(String username) {

        StudentProfile student = studentProfileRepository.findStudentByEmail(username)
                .orElseThrow(() -> new NotFoundException("Student not found!"));

        Program program = student.getProgram();

        List<ProgramSubject> programSubjects =
                programSubjectRepository.findAllWithSubjectsByProgram(program);

        Map<Integer, List<ProgramSubject>> grouped =
                programSubjects.stream()
                        .collect(Collectors.groupingBy(ProgramSubject::getSemester));

        List<ProgramResponse.SubjectBySemester> semesterList = new ArrayList<>();

        for (var entry : grouped.entrySet()) {

            List<ProgramResponse.SubjectInfo> subjects = entry.getValue().stream().map(ps -> {

                Subject s = ps.getSubject();

                // ===== prerequisite =====
                ProgramResponse.PrerequisiteInfo pre = null;

                if (ps.getPrerequisiteSubject() != null) {
                    Subject p = ps.getPrerequisiteSubject();

                    pre = new ProgramResponse.PrerequisiteInfo();
                    pre.setId(p.getId());
                    pre.setCode(p.getCode());
                    pre.setName(p.getName());
                }

                // ===== subject =====
                ProgramResponse.SubjectInfo info = new ProgramResponse.SubjectInfo();
                info.setId(s.getId());
                info.setCode(s.getCode());
                info.setName(s.getName());
                info.setCredits(s.getCredits());
                info.setIsRequired(ps.getIsRequired());
                info.setPrerequisite(pre);

                return info;

            }).toList();

            ProgramResponse.SubjectBySemester sem = new ProgramResponse.SubjectBySemester();
            sem.setSemester(entry.getKey());
            sem.setSubjectsBySemester(subjects);

            semesterList.add(sem);
        }

        semesterList.sort(Comparator.comparing(ProgramResponse.SubjectBySemester::getSemester));

        // ===== program info =====
        ProgramResponse.ProgramInfo programInfo = new ProgramResponse.ProgramInfo();
        programInfo.setId(program.getId());
        programInfo.setCode(program.getCode());
        programInfo.setName(program.getName());
        programInfo.setTotalCredits(program.getTotalCredits());
        programInfo.setDurationYears(program.getDurationYears());

        ProgramResponse response = new ProgramResponse();
        response.setProgram(programInfo);
        response.setSubjectsBySemester(semesterList);

        return response;
    }

    public StudentTimetableResponse getMySchedule(String username, Long semesterId, LocalDate fromDate, LocalDate toDate) {
        StudentProfile student = studentProfileRepository.findStudentByEmail(username)
                .orElseThrow(() -> new NotFoundException("Student not found!"));

        List<Enrollment> enrollments = enrollmentRepository
                .findByStudentIdAndSemesterIdAndStatus(
                        student.getId(),
                        semesterId,
                        EnrollmentStatus.ENROLLED
                );

        if (enrollments.isEmpty()) {
            return new StudentTimetableResponse();
        }

        List<ClassEntity> classes = enrollments.stream()
                .map(Enrollment::getClassEntity)
                .toList();

        List<ClassSchedule> schedules = classScheduleRepository
                .findByClassEntityIn(classes);

        List<ClassSchedule> filtered = schedules.stream()
                .filter(s -> {

                    if (fromDate == null && toDate == null) {
                        return true;
                    }

                    LocalDate start = s.getWeekStart();
                    LocalDate end = s.getWeekEnd();

                    if (fromDate != null && end.isBefore(fromDate)) {
                        return false;
                    }

                    if (toDate != null && start.isAfter(toDate)) {
                        return false;
                    }

                    return true;

                })
                .toList();

        Map<Integer, List<ClassSchedule>> grouped = filtered.stream()
                .collect(Collectors.groupingBy(ClassSchedule::getDayOfWeek));

        List<StudentTimetableResponse.DaySchedule> timetable = new ArrayList<>();

        for (int day = 0; day <= 6; day++) {

            List<ClassSchedule> daySchedules = grouped.getOrDefault(day, Collections.emptyList());

            List<StudentTimetableResponse.ClassItem> classItems = daySchedules.stream()
                    .map(s -> {

                        ClassEntity c = s.getClassEntity();

                        StudentTimetableResponse.ClassItem item = new StudentTimetableResponse.ClassItem();
                        item.setClassId(c.getId());
                        item.setClassCode(c.getClassCode());
                        item.setSubjectName(c.getSubject().getName());
                        item.setRoom(s.getRoom());
                        item.setTeacherName(c.getTeacher().getUser().getFullName());
                        item.setStartPeriod(s.getStartPeriod());
                        item.setEndPeriod(s.getEndPeriod());

                        return item;

                    })
                    .sorted(Comparator.comparing(StudentTimetableResponse.ClassItem::getStartPeriod))
                    .toList();

            StudentTimetableResponse.DaySchedule daySchedule = new StudentTimetableResponse.DaySchedule();
            daySchedule.setDayOfWeek(day);
            daySchedule.setClasses(classItems);

            timetable.add(daySchedule);
        }

        StudentTimetableResponse response = new StudentTimetableResponse();

        response.setTimetable(timetable);

        return response;
    }

    public List<StudentTuitionResponse> getMyTuition(String username, Long semesterId) {
        StudentProfile student = studentProfileRepository.findStudentByEmail(username)
                .orElseThrow(() -> new NotFoundException("Student not found!"));

        List<TuitionFee> tuitionFees = semesterId != null
                ? tuitionFeeRepository.findByStudentIdAndSemesterIdOrderByCreatedAtDesc(student.getUser().getId(), semesterId)
                : tuitionFeeRepository.findByStudentIdOrderByCreatedAtDesc(student.getUser().getId());

        return tuitionFees.stream()
                .map(StudentTuitionResponse::from)
                .toList();
    }

    public List<StudentPaymentHistoryResponse> getMyPaymentHistory(String username, PaymentStatus status) {
        StudentProfile student = studentProfileRepository.findStudentByEmail(username)
                .orElseThrow(() -> new NotFoundException("Student not found!"));

        List<Payment> payments = status != null
                ? paymentRepository.findByStudentIdAndStatusOrderByCreatedAtDesc(student.getUser().getId(), status)
                : paymentRepository.findByStudentIdOrderByCreatedAtDesc(student.getUser().getId());

        return payments.stream()
                .map(StudentPaymentHistoryResponse::from)
                .toList();
    }
}
