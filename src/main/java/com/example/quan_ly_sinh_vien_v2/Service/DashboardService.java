package com.example.quan_ly_sinh_vien_v2.Service;

import com.example.quan_ly_sinh_vien_v2.DTO.Response.Dashboard.AdminDashboardResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.Dashboard.TeacherDashboardResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.Dashboard.StudentDashboardResponse;
import com.example.quan_ly_sinh_vien_v2.Exception.NotFoundException;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.*;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.LetterGrade;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.PaymentStatus;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.Role;
import com.example.quan_ly_sinh_vien_v2.Repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Dashboard Service
 * Cung cấp các phương thức để lấy dữ liệu dashboard cho Admin, Teacher, Student
 */
@Service
public class DashboardService {
    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final TeacherProfileRepository teacherProfileRepository;
    private final ClassEntityRepository classEntityRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final GradeRepository gradeRepository;
    private final TuitionFeeRepository tuitionFeeRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationRepository notificationRepository;
    private final SubjectRepository subjectRepository;
    private final ProgramRepository programRepository;
    private final DepartmentRepository departmentRepository;
    private final SemesterRepository semesterRepository;
    private final AttendanceRepository attendanceRepository;
    private final StudentService studentService;
    private final TeacherService teacherService;

    public DashboardService(
            UserRepository userRepository,
            StudentProfileRepository studentProfileRepository,
            TeacherProfileRepository teacherProfileRepository,
            ClassEntityRepository classEntityRepository,
            EnrollmentRepository enrollmentRepository,
            GradeRepository gradeRepository,
            TuitionFeeRepository tuitionFeeRepository,
            PaymentRepository paymentRepository,
            NotificationRepository notificationRepository,
            SubjectRepository subjectRepository,
            ProgramRepository programRepository,
            DepartmentRepository departmentRepository,
            SemesterRepository semesterRepository,
            AttendanceRepository attendanceRepository,
            StudentService studentService,
            TeacherService teacherService) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.teacherProfileRepository = teacherProfileRepository;
        this.classEntityRepository = classEntityRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.gradeRepository = gradeRepository;
        this.tuitionFeeRepository = tuitionFeeRepository;
        this.paymentRepository = paymentRepository;
        this.notificationRepository = notificationRepository;
        this.subjectRepository = subjectRepository;
        this.programRepository = programRepository;
        this.departmentRepository = departmentRepository;
        this.semesterRepository = semesterRepository;
        this.attendanceRepository = attendanceRepository;
        this.studentService = studentService;
        this.teacherService = teacherService;
    }

    /**
     * Lấy Admin Dashboard
     * Cung cấp tổng quát toàn bộ hệ thống
     */
    public AdminDashboardResponse getAdminDashboard() {
        AdminDashboardResponse response = new AdminDashboardResponse();

        // Thống kê người dùng
        long totalStudents = userRepository.countByRole(Role.ROLE_STUDENT);
        long totalTeachers = userRepository.countByRole(Role.ROLE_TEACHER);
        long totalAdmins = userRepository.countByRole(Role.ROLE_ADMIN);
        long totalActiveUsers = userRepository.countByIsActive(true);
        long totalInactiveUsers = userRepository.countByIsActive(false);

        response.setTotalStudents(totalStudents);
        response.setTotalTeachers(totalTeachers);
        response.setTotalAdmins(totalAdmins);
        response.setTotalActiveUsers(totalActiveUsers);
        response.setTotalInactiveUsers(totalInactiveUsers);

        // Thống kê lớp học
        long totalClasses = classEntityRepository.count();
        long totalEnrollments = enrollmentRepository.count();

        response.setTotalClasses(totalClasses);
        response.setTotalEnrollments(totalEnrollments);
        response.setTotalOpenClasses(
                classEntityRepository.countByStatus(
                        com.example.quan_ly_sinh_vien_v2.Modal.Enum.ClassStatus.OPEN)
        );
        response.setTotalClosedClasses(
                classEntityRepository.countByStatus(
                        com.example.quan_ly_sinh_vien_v2.Modal.Enum.ClassStatus.CLOSE)
        );

        // Thống kê học tập
        long totalSubjects = subjectRepository.count();
        long totalPrograms = programRepository.count();
        long totalDepartments = departmentRepository.count();
        long totalSemesters = semesterRepository.count();

        response.setTotalSubjects(totalSubjects);
        response.setTotalPrograms(totalPrograms);
        response.setTotalDepartments(totalDepartments);
        response.setTotalSemesters(totalSemesters);

        // Thống kê tài chính
        List<Payment> payments = paymentRepository.findAll();
        double totalCollected = payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
                .mapToDouble(Payment::getAmount)
                .sum();
        double totalPending = payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.PENDING)
                .mapToDouble(Payment::getAmount)
                .sum();

        response.setTotalTuitionCollected(totalCollected);
        response.setTotalTuitionPending(totalPending);
        response.setTotalPaymentTransactions((long) payments.size());
        response.setTotalPaidPayments(payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.SUCCESS).count());
        response.setTotalPendingPayments(payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.PENDING).count());

        // Thống kê thông báo
        response.setTotalNotificationsSent(notificationRepository.count());
        response.setTotalUnreadNotifications(
                notificationRepository.countByIsRead(false)
        );

        // Thống kê nâng cao
        response.setAverageStudentsPerClass(
                totalEnrollments > 0 ? (int) (totalEnrollments / Math.max(totalClasses, 1)) : 0
        );

        // GPA trung bình
        List<StudentProfile> students = studentProfileRepository.findAll();
        Double avgGPA = students.stream()
                .filter(s -> s.getGpa() != null)
                .mapToDouble(StudentProfile::getGpa)
                .average()
                .orElse(0.0);
        response.setAverageGPA(avgGPA);

        // Số sinh viên rớt
        List<Grade> allGrades = gradeRepository.findAll();
        long failedGrades = allGrades.stream()
                .filter(g -> g.getLetterGrade() == LetterGrade.F)
                .count();
        response.setTotalFailedGrades(failedGrades);

        // Thông tin cập nhật
        response.setLastUpdated(LocalDateTime.now());
        response.setSystemStatus("ONLINE");

        return response;
    }

    /**
     * Lấy Teacher Dashboard
     * Cung cấp thông tin về lớp học, sinh viên, điểm số của giáo viên
     */
    public TeacherDashboardResponse getTeacherDashboard(String email) {
        TeacherProfile teacher = teacherProfileRepository.findTeacherProfileByEmail(email)
                .orElseThrow(() -> new NotFoundException("Teacher not found!"));

        TeacherDashboardResponse response = new TeacherDashboardResponse();

        // Thông tin giáo viên
        response.setTeacherInfo(teacherService.getMyProfile(email));
        response.setDepartmentName(teacher.getDepartment() != null ? 
                teacher.getDepartment().getName() : "N/A");

        // Thống kê lớp học
        List<ClassEntity> classes = classEntityRepository.findByTeacher(teacher);
        response.setTotalClasses((long) classes.size());
        response.setClasses(classes.stream()
                .map(c -> new com.example.quan_ly_sinh_vien_v2.DTO.Response.ClassResponse())
                .collect(Collectors.toList()));

        // Tổng số sinh viên
        long totalStudents = classes.stream()
                .mapToLong(c -> c.getCurrentStudents() != null ? c.getCurrentStudents() : 0)
                .sum();
        response.setTotalStudents(totalStudents);

        // Thống kê điểm
        List<Grade> teacherGrades = new ArrayList<>();
        for (ClassEntity classEntity : classes) {
            List<Grade> classGrades = gradeRepository.findAll().stream()
                    .filter(g -> g.getEnrollment() != null && 
                            g.getEnrollment().getClassEntity() != null &&
                            g.getEnrollment().getClassEntity().getId().equals(classEntity.getId()))
                    .collect(Collectors.toList());
            teacherGrades.addAll(classGrades);
        }

        response.setTotalGradesPosted((int) teacherGrades.stream()
                .filter(Grade::getIsPublished)
                .count());
        response.setTotalGradesPending((int) teacherGrades.stream()
                .filter(g -> !g.getIsPublished())
                .count());

        // Tổng enrollments
        long totalEnrollments = classes.stream()
                .flatMap(c -> enrollmentRepository.findByClassEntity(c).stream())
                .count();
        response.setTotalEnrollments(totalEnrollments);

        // Thống kê điểm GPA
        Double avgGPA = teacherGrades.stream()
                .mapToDouble(Grade::getScore)
                .average()
                .orElse(0.0);
        response.setAverageClassGPA(avgGPA);

        // Sinh viên rớt
        long failedStudents = teacherGrades.stream()
                .filter(g -> g.getLetterGrade() == LetterGrade.F)
                .count();
        response.setTotalFailedStudents(failedStudents);

        // Sinh viên xuất sắc (A, B)
        long excellentStudents = teacherGrades.stream()
                .filter(g -> g.getLetterGrade() == LetterGrade.A || 
                        g.getLetterGrade() == LetterGrade.B)
                .count();
        response.setTotalExcellentStudents(excellentStudents);

        // Thông tin lớp lớn nhất
        if (!classes.isEmpty()) {
            ClassEntity largestClass = classes.stream()
                    .max((c1, c2) -> Integer.compare(
                            c1.getCurrentStudents() != null ? c1.getCurrentStudents() : 0,
                            c2.getCurrentStudents() != null ? c2.getCurrentStudents() : 0))
                    .orElse(null);

            if (largestClass != null) {
                response.setLargestClassName(largestClass.getClassCode());
                response.setLargestClassSize(largestClass.getCurrentStudents());
            }

            // Lớp nhỏ nhất
            ClassEntity smallestClass = classes.stream()
                    .min((c1, c2) -> Integer.compare(
                            c1.getCurrentStudents() != null ? c1.getCurrentStudents() : 0,
                            c2.getCurrentStudents() != null ? c2.getCurrentStudents() : 0))
                    .orElse(null);

            if (smallestClass != null) {
                response.setSmallestClassName(smallestClass.getClassCode());
                response.setSmallestClassSize(smallestClass.getCurrentStudents());
            }
        }

        response.setLastUpdated(LocalDateTime.now());

        return response;
    }

    /**
     * Lấy Student Dashboard
     * Cung cấp thông tin cá nhân sinh viên, điểm số, lịch học, học phí
     */
    public StudentDashboardResponse getStudentDashboard(String email) {
        StudentProfile student = studentProfileRepository.findStudentByEmail(email)
                .orElseThrow(() -> new NotFoundException("Student not found!"));

        StudentDashboardResponse response = new StudentDashboardResponse();

        // Thông tin sinh viên
        response.setStudentInfo(studentService.getProfile(email));
        response.setProgramName(student.getProgram() != null ? 
                student.getProgram().getName() : "N/A");
        response.setDepartmentName(student.getDepartment() != null ? 
                student.getDepartment().getName() : "N/A");
        response.setEnrollmentYear(student.getEnrollmentYear());

        // Thống kê học tập
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(student.getId());
        response.setTotalEnrolledClasses(enrollments.size());
        response.setCurrentGPA(student.getGpa() != null ? student.getGpa() : 0.0);
        response.setTotalCompletedCredits(student.getAccumulatedCredits() != null ? 
                student.getAccumulatedCredits() : 0);
        response.setStudentStatus(student.getStatus() != null ? 
                student.getStatus().toString() : "ACTIVE");

        // Thống kê điểm số
        List<Grade> studentGrades = new ArrayList<>();
        for (Enrollment e : enrollments) {
            // Lấy tất cả grades cho enrollment này
            List<Grade> gradeList = gradeRepository.findAll().stream()
                    .filter(g -> g.getEnrollment() != null && g.getEnrollment().getId().equals(e.getId()))
                    .collect(Collectors.toList());
            studentGrades.addAll(gradeList);
        }

        double avgScore = 0.0;
        int passedCount = 0;
        int failedCount = 0;

        if (!studentGrades.isEmpty()) {
            avgScore = studentGrades.stream()
                    .filter(g -> g.getScore() != null)
                    .mapToDouble(Grade::getScore)
                    .average()
                    .orElse(0.0);

            passedCount = (int) studentGrades.stream()
                    .filter(g -> g.getLetterGrade() != LetterGrade.F)
                    .count();

            failedCount = (int) studentGrades.stream()
                    .filter(g -> g.getLetterGrade() == LetterGrade.F)
                    .count();
        }

        response.setAverageScore(avgScore);
        response.setTotalPassedSubjects(passedCount);
        response.setTotalFailedSubjects(failedCount);

        // Thống kê tham dự
        List<Attendance> attendances = new ArrayList<>();
        for (Enrollment enrollment : enrollments) {
            List<Attendance> enrollmentAttendances = attendanceRepository.findAll();
            for (Attendance att : enrollmentAttendances) {
                if (att.getEnrollment() != null && att.getEnrollment().getId().equals(enrollment.getId())) {
                    attendances.add(att);
                }
            }
        }
        
        if (!attendances.isEmpty()) {
            long presentDays = attendances.stream()
                    .filter(a -> a.getStatus() != null && 
                            a.getStatus().toString().equals("PRESENT"))
                    .count();
            double attendanceRate = (presentDays * 100.0) / attendances.size();
            response.setAttendanceRate(attendanceRate);

            long absentDays = attendances.stream()
                    .filter(a -> a.getStatus() != null && 
                            a.getStatus().toString().equals("ABSENT"))
                    .count();
            response.setTotalAbsentDays((int) absentDays);
        }

        // Thông tin học phí
        List<TuitionFee> tuitions = tuitionFeeRepository.findByStudentIdOrderByCreatedAtDesc(student.getId());
        double totalTuition = tuitions.stream()
                .mapToDouble(TuitionFee::getAmount)
                .sum();
        response.setTotalTuitionFee(totalTuition);

        // Lấy payments qua TuitionFees
        List<Payment> payments = new ArrayList<>();
        for (TuitionFee tuition : tuitions) {
            List<Payment> tuitionPayments = paymentRepository.findAll().stream()
                    .filter(p -> p.getTuitionFee() != null && p.getTuitionFee().getId().equals(tuition.getId()))
                    .collect(Collectors.toList());
            payments.addAll(tuitionPayments);
        }

        double paidAmount = payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
                .mapToDouble(Payment::getAmount)
                .sum();
        response.setPaidAmount(paidAmount);
        response.setRemainingAmount(totalTuition - paidAmount);

        String tuitionStatus = paidAmount >= totalTuition ? "PAID" : 
                paidAmount > 0 ? "PARTIAL" : "PENDING";
        response.setTuitionStatus(tuitionStatus);

        response.setLastUpdated(LocalDateTime.now());

        return response;
    }
}









