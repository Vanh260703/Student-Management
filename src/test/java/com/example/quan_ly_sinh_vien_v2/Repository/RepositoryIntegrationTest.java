package com.example.quan_ly_sinh_vien_v2.Repository;

import com.example.quan_ly_sinh_vien_v2.Modal.Entity.AcademicYear;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Department;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Payment;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Program;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.ProgramSubject;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Semester;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.StudentProfile;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Subject;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.TuitionFee;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.User;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.PaymentMethod;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.PaymentStatus;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.Role;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.SemesterName;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.StudentStatus;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.TuitionStatus;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RepositoryIntegrationTest {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private StudentProfileRepository studentProfileRepository;
    @Autowired
    private ProgramSubjectRepository programSubjectRepository;
    @Autowired
    private EntityManager entityManager;

    @Test
    void paymentRepositoryShouldFilterAndSortByCreatedAtDesc() throws Exception {
        String suffix = String.valueOf(System.nanoTime());
        User student = persistUser("student" + suffix + "@example.com", "personal" + suffix + "@example.com");
        Semester semester = persistSemester("AY-" + suffix);

        Payment older = persistPayment(student, semester, "TXN-OLD-" + suffix, PaymentStatus.PENDING);
        Thread.sleep(5);
        Payment newer = persistPayment(student, semester, "TXN-NEW-" + suffix, PaymentStatus.SUCCESS);

        entityManager.flush();
        entityManager.clear();

        List<Payment> all = paymentRepository.findByStudentIdOrderByCreatedAtDesc(student.getId());
        List<Payment> success = paymentRepository.findByStudentIdAndStatusOrderByCreatedAtDesc(student.getId(), PaymentStatus.SUCCESS);

        assertEquals(2, all.size());
        assertEquals("TXN-NEW-" + suffix, all.get(0).getTransactionCode());
        assertEquals(1, success.size());
        assertEquals(newer.getTransactionCode(), success.get(0).getTransactionCode());
        assertTrue(all.get(0).getCreatedAt().isAfter(all.get(1).getCreatedAt()) || all.get(0).getCreatedAt().isEqual(all.get(1).getCreatedAt()));
    }

    @Test
    void studentProfileRepositoryShouldSearchByNormalizeName() {
        String suffix = String.valueOf(System.nanoTime());
        Department department = persistDepartment("D" + suffix, "Department " + suffix);
        Program program = persistProgram(department, "P" + suffix, "Program " + suffix);

        User user = persistUser("search" + suffix + "@example.com", "search-personal" + suffix + "@example.com");
        user.setFullName("Nguyen Van Search");
        user.setNormalizeName("nguyenvansearch");
        entityManager.merge(user);

        StudentProfile student = new StudentProfile();
        student.setUser(user);
        student.setStudentCode("SV" + suffix);
        student.setDepartment(department);
        student.setProgram(program);
        student.setEnrollmentYear(2026);
        student.setClassName("SE01");
        student.setStatus(StudentStatus.ACTIVE);
        entityManager.persist(student);
        entityManager.flush();
        entityManager.clear();

        List<StudentProfile> result = studentProfileRepository.searchStudents(
                department.getId(), "nguyenvan", program.getId(), 2026, StudentStatus.ACTIVE, null, null
        );

        assertEquals(1, result.size());
        assertEquals("SV" + suffix, result.get(0).getStudentCode());
    }

    @Test
    void programSubjectRepositoryShouldReturnIntersectionOfSemesterAndRequired() {
        String suffix = String.valueOf(System.nanoTime());
        Department department = persistDepartment("D" + suffix, "Department " + suffix);
        Program program = persistProgram(department, "P" + suffix, "Program " + suffix);
        Subject subjectOne = persistSubject(department, "S1" + suffix, "Subject 1");
        Subject subjectTwo = persistSubject(department, "S2" + suffix, "Subject 2");

        ProgramSubject match = new ProgramSubject();
        match.setProgram(program);
        match.setSubject(subjectOne);
        match.setSemester(2);
        match.setIsRequired(true);
        entityManager.persist(match);

        ProgramSubject nonMatch = new ProgramSubject();
        nonMatch.setProgram(program);
        nonMatch.setSubject(subjectTwo);
        nonMatch.setSemester(2);
        nonMatch.setIsRequired(false);
        entityManager.persist(nonMatch);

        entityManager.flush();
        entityManager.clear();

        List<ProgramSubject> result = programSubjectRepository.findAllByProgramAndSemesterAndIsRequired(program, 2, true);

        assertEquals(1, result.size());
        assertEquals(subjectOne.getCode(), result.get(0).getSubject().getCode());
    }

    private Payment persistPayment(User student, Semester semester, String transactionCode, PaymentStatus status) {
        TuitionFee tuitionFee = new TuitionFee();
        tuitionFee.setStudent(student);
        tuitionFee.setSemester(semester);
        tuitionFee.setAmount(1_600_000D);
        tuitionFee.setDiscount(0D);
        tuitionFee.setFinalAmount(1_600_000D);
        tuitionFee.setDueDate(LocalDate.now().plusDays(10));
        tuitionFee.setStatus(TuitionStatus.PENDING);
        entityManager.persist(tuitionFee);

        Payment payment = new Payment();
        payment.setStudent(student);
        payment.setTuitionFee(tuitionFee);
        payment.setAmount(1_600_000D);
        payment.setMethod(PaymentMethod.MOMO);
        payment.setTransactionCode(transactionCode);
        payment.setStatus(status);
        payment.setPaidAt(LocalDateTime.now());
        payment.setGatewayResponse("{}");
        entityManager.persist(payment);
        return payment;
    }

    private Semester persistSemester(String academicYearName) {
        AcademicYear academicYear = new AcademicYear();
        academicYear.setName(academicYearName);
        academicYear.setStartDate(LocalDate.of(2025, 9, 1));
        academicYear.setEndDate(LocalDate.of(2026, 6, 30));
        academicYear.setIsCurrent(true);
        entityManager.persist(academicYear);

        Semester semester = new Semester();
        semester.setAcademicYear(academicYear);
        semester.setName(SemesterName.HK1);
        semester.setSemesterNumber(1);
        semester.setStartDate(LocalDate.of(2025, 9, 1));
        semester.setEndDate(LocalDate.of(2026, 1, 15));
        semester.setRegistrationStart(Instant.now().minusSeconds(3600));
        semester.setRegistrationEnd(Instant.now().plusSeconds(3600));
        semester.setIsActive(true);
        entityManager.persist(semester);
        return semester;
    }

    private Department persistDepartment(String code, String name) {
        Department department = new Department();
        department.setCode(code);
        department.setName(name);
        entityManager.persist(department);
        return department;
    }

    private Program persistProgram(Department department, String code, String name) {
        Program program = new Program();
        program.setDepartment(department);
        program.setCode(code);
        program.setName(name);
        program.setNormalizeName(name.toLowerCase().replace(" ", ""));
        program.setTotalCredits(130);
        program.setDurationYears(4);
        entityManager.persist(program);
        return program;
    }

    private Subject persistSubject(Department department, String code, String name) {
        Subject subject = new Subject();
        subject.setDepartment(department);
        subject.setCode(code);
        subject.setName(name);
        subject.setNormalizeName(name.toLowerCase().replace(" ", ""));
        subject.setCredits(3);
        subject.setIsActive(true);
        entityManager.persist(subject);
        return subject;
    }

    private User persistUser(String email, String personalEmail) {
        User user = new User();
        user.setEmail(email);
        user.setPassword("encoded");
        user.setFullName("Student");
        user.setPersonalEmail(personalEmail);
        user.setNormalizeName("student");
        user.setRole(Role.ROLE_STUDENT);
        entityManager.persist(user);
        return user;
    }
}
