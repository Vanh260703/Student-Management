package com.example.quan_ly_sinh_vien_v2.Service.Admin;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.Admin.CreateStudentRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Request.Admin.UpdateStudentRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.StudentProfileResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.StudentResponse;
import com.example.quan_ly_sinh_vien_v2.Exception.AlreadyExistsException;
import com.example.quan_ly_sinh_vien_v2.Exception.CreateFailException;
import com.example.quan_ly_sinh_vien_v2.Exception.NotFoundException;
import com.example.quan_ly_sinh_vien_v2.Exception.UpdateFailException;
import com.example.quan_ly_sinh_vien_v2.Helper;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.*;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.Gender;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.Role;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.StudentStatus;
import com.example.quan_ly_sinh_vien_v2.Repository.DepartmentRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.ProgramRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.StudentProfileRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.UserRepository;
import com.example.quan_ly_sinh_vien_v2.Service.MailService;
import com.example.quan_ly_sinh_vien_v2.Util.GenerateCode;
import com.example.quan_ly_sinh_vien_v2.Util.GenerateSchoolEmail;
import com.example.quan_ly_sinh_vien_v2.Util.PasswordGenerater;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.sql.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminStudentService {
    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final ProgramRepository programRepository;
    private final DepartmentRepository departmentRepository;
    private final MailService mailService;

    @Autowired PasswordEncoder passwordEncoder;

    public AdminStudentService(UserRepository userRepository, StudentProfileRepository studentProfileRepository, ProgramRepository programRepository, DepartmentRepository departmentRepository, MailService mailService) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.programRepository = programRepository;
        this.departmentRepository = departmentRepository;
        this.mailService = mailService;
    }

    // Get students
    public List<StudentResponse> getStudents(String search, Long departmentId, Long programId, Integer enrollmentYear, StudentStatus status, Float gpaMin, Float gpaMax) {
        String keyword = null;

        if (search != null && !search.trim().isEmpty()) {
            keyword = search.trim().toLowerCase();
        }

        List<StudentProfile> students = studentProfileRepository.searchStudents(
                departmentId,
                keyword,
                programId,
                enrollmentYear,
                status,
                gpaMin,
                gpaMax
        );

        return students.stream()
                .map(StudentResponse::from)
                .toList();
    }

    // Details student
    public StudentResponse getDetailsStudent(Long studentId) {
        StudentProfile student = studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found!"));

        return StudentResponse.from(student);
    }

    // Create student
    public StudentResponse createStudent(@Valid CreateStudentRequest request) {
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new NotFoundException("Department not found!"));

        Program program = programRepository.findById(request.getProgramId())
                .orElseThrow(() -> new NotFoundException("Program not found!"));

        if (program.getDepartment() != department) {
            throw new NotFoundException("Program is not found in department!");
        }

        if (userRepository.existsUserByPersonalEmail(request.getPersonalEmail())) {
            throw new CreateFailException("Personal email already exists!");
        }

        String studentCode = GenerateCode.generateStudentCode(studentProfileRepository);

        String email = GenerateSchoolEmail.generateSchoolEmail(request.getFullName(), studentCode, Role.ROLE_STUDENT);

        String rawPassword = PasswordGenerater.generate(12);
        System.out.println("PASSWORD: " + rawPassword);

        User user = new User();

        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setFullName(request.getFullName());
        user.setNormalizeName(Helper.normalizeFullName(request.getFullName()));
        user.setPhone(request.getPhone());
        user.setPersonalEmail(request.getPersonalEmail());
        user.setGender(request.getGender());
        user.setAddress(request.getAddress());
        user.setDateOfBirth(request.getDayOfBirth());
        user.setRole(Role.ROLE_STUDENT);

        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }

        userRepository.save(user);

        StudentProfile student = new StudentProfile();

        student.setUser(user);
        student.setStudentCode(studentCode);
        student.setProgram(program);
        student.setDepartment(department);
        student.setClassName(request.getClassName());
        student.setEnrollmentYear(Year.now().getValue());
        student.setStatus(StudentStatus.ACTIVE);

        studentProfileRepository.save(student);

        mailService.sendEmail(
                request.getPersonalEmail(),
                "Cấp tài khoản cho hệ thống quản lý sinh viên",
                "Email đăng nhập: " + email +
                        "\n Mật khẩu: " + rawPassword
        );

        user.setIsSendMail(true);

        return StudentResponse.from(student);
    }

    // Import student
    public void importStudents(MultipartFile file) {
        List<StudentProfile> students = new ArrayList<>();
        List<User> users = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                Cell firstCell = row.getCell(0);

                if (firstCell == null || firstCell.getCellType() == CellType.BLANK) {
                    continue;
                }

                // Fields in excel
                String fullName = Helper.getCellString(row, 0);
                String personalEmail = Helper.getCellString(row, 1);
                String genderStr = Helper.getCellString(row, 2);
                String phone = Helper.getCellString(row, 3);
                String departmentIdStr = Helper.getCellString(row, 4);
                String programIdStr = Helper.getCellString(row, 5);
                String className = Helper.getCellString(row, 6);
                String address = Helper.getCellString(row, 7);

                Gender gender;

                switch (genderStr.trim().toLowerCase()) {
                    case "nam":
                        gender = Gender.MALE;
                        break;
                    case "nữ":
                        gender = Gender.FEMALE;
                        break;
                    case "khác":
                        gender = Gender.OTHER;
                        break;
                    default:
                        throw new RuntimeException("Gender không hợp lệ tại dòng " + row.getRowNum());
                }

                if (userRepository.existsUserByPersonalEmail(personalEmail)) {
                    throw new CreateFailException("Personal email already exists!");
                }

                Long departmentId = Long.parseLong(departmentIdStr);
                Long programId = Long.parseLong(programIdStr);

                Department department = departmentRepository.findById(departmentId)
                        .orElseThrow(() -> new NotFoundException("Department not found!"));

                Program program = programRepository.findById(programId)
                        .orElseThrow(() -> new NotFoundException("Program not found!"));

                if (program.getDepartment() != department) {
                    throw new NotFoundException("Program is not found in department!");
                }

                String studentCode = GenerateCode.generateStudentCode(studentProfileRepository);

                String email = GenerateSchoolEmail.generateSchoolEmail(fullName, studentCode, Role.ROLE_STUDENT);

                String rawPassword = PasswordGenerater.generate(12);
                System.out.println("PASSWORD: " + rawPassword);

                User user = new User();

                user.setEmail(email);
                user.setPassword(passwordEncoder.encode(rawPassword));
                user.setFullName(fullName);
                user.setNormalizeName(Helper.normalizeFullName(fullName));
                user.setPhone(phone);
                user.setPersonalEmail(personalEmail);
                user.setGender(gender);
                user.setAddress(address);
                user.setRole(Role.ROLE_STUDENT);

                userRepository.save(user);
                users.add(user);

                StudentProfile student = new StudentProfile();

                student.setUser(user);
                student.setStudentCode(studentCode);
                student.setProgram(program);
                student.setDepartment(department);
                student.setClassName(className);
                student.setEnrollmentYear(Year.now().getValue());
                student.setStatus(StudentStatus.ACTIVE);

                studentProfileRepository.save(student);
                students.add(student);
            }

            userRepository.saveAll(users);
            studentProfileRepository.saveAll(students);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Import failed");
        }
    }

    @Transactional
    public StudentProfileResponse updateStudent(Long studentId, UpdateStudentRequest request) {
            System.out.println("HÀM ĐƯỢC GỌI!!!!");
            System.out.println(studentId);

            StudentProfile student = studentProfileRepository.findById(studentId)
                    .orElseThrow(() -> new NotFoundException("Student not found!"));

            User user = student.getUser();

            if (request.getFullName() != null && !request.getFullName().isBlank()) {
                String fullName = request.getFullName().trim();

                if (fullName.length() < 2) {
                    throw new UpdateFailException("Full name must be at least 2 characters");
                }

                user.setFullName(fullName);

                String normalize = Helper.normalizeFullName(fullName);
                user.setNormalizeName(normalize);
            }

            if (request.getPhone() != null && !request.getPhone().isBlank()) {
                String phone = request.getPhone().trim();

                if (!phone.matches("^(0|\\+84)[0-9]{9}$")) {
                    throw new UpdateFailException("Invalid phone number");
                }

                user.setPhone(phone);
            }

            if (request.getPersonalEmail() != null && !request.getPersonalEmail().isBlank()) {
                String email = request.getPersonalEmail().trim();

                if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                    throw new UpdateFailException("Invalid email format");
                }

                if (userRepository.existsUserByPersonalEmail(request.getPersonalEmail()))
                    throw new AlreadyExistsException("Personal email already in use");

                user.setPersonalEmail(email);
            }

            if (request.getDayOfBirth() != null) {
                if (request.getDayOfBirth().isAfter(LocalDate.now())) {
                    throw new CreateFailException("Date of birth cannot be in the future");
                }
                user.setDateOfBirth(request.getDayOfBirth());
            }

            if (request.getAddress() != null) {
                user.setAddress(request.getAddress().trim());
            }

            if (request.getGender() != null) {
                user.setGender(request.getGender());
            }

            if (request.getIsActive() != null) {
                user.setIsActive(request.getIsActive());
            }

            user.setUpdatedAt(LocalDateTime.now());


            if (request.getEnrollmentYear() != null) {
                student.setEnrollmentYear(request.getEnrollmentYear());
            }

            if (request.getClassName() != null) {
                student.setClassName(request.getClassName().trim());
            }

            if (request.getStatus() != null) {
                student.setStatus(request.getStatus());
            }

            userRepository.save(user);
            studentProfileRepository.save(student);

            return StudentProfileResponse.from(student);
    }
}
