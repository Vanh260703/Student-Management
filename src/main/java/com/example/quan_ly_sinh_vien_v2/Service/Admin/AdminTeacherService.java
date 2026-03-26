package com.example.quan_ly_sinh_vien_v2.Service.Admin;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.Admin.CreateTeacherRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.TeacherResponse;
import com.example.quan_ly_sinh_vien_v2.Exception.AlreadyExistsException;
import com.example.quan_ly_sinh_vien_v2.Exception.CreateFailException;
import com.example.quan_ly_sinh_vien_v2.Exception.NotFoundException;
import com.example.quan_ly_sinh_vien_v2.Helper;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Department;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.TeacherProfile;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.User;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.Gender;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.Role;
import com.example.quan_ly_sinh_vien_v2.Repository.DepartmentRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.TeacherProfileRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.UserRepository;
import com.example.quan_ly_sinh_vien_v2.Service.MailService;
import com.example.quan_ly_sinh_vien_v2.Util.GenerateCode;
import com.example.quan_ly_sinh_vien_v2.Util.GenerateSchoolEmail;
import com.example.quan_ly_sinh_vien_v2.Util.PasswordGenerater;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AdminTeacherService {
    private final UserRepository userRepository;
    private final TeacherProfileRepository teacherProfileRepository;
    private final DepartmentRepository departmentRepository;
    private final MailService mailService;

    @Autowired PasswordEncoder passwordEncoder;

    public AdminTeacherService(UserRepository userRepository, TeacherProfileRepository teacherProfileRepository, DepartmentRepository departmentRepository, MailService mailService) {
        this.userRepository = userRepository;
        this.teacherProfileRepository = teacherProfileRepository;
        this.departmentRepository = departmentRepository;
        this.mailService = mailService;
    }

    // Get teachers
    public List<TeacherResponse> getTeachers(String search, Long departmentId, String degree) {
        String keyword = null;

        if (search != null && !search.trim().isEmpty()) {
            keyword = search.trim().toLowerCase();
        }

        List<TeacherProfile> teachers = teacherProfileRepository.searchTeachers(departmentId,search, degree);

        return teachers.stream().map(TeacherResponse::from).toList();
    }

    // Create teacher
    @Transactional
    public TeacherResponse createTeacher(@Valid CreateTeacherRequest request) {
        if (userRepository.existsUserByPersonalEmail(request.getPersonalEmail())) {
            throw new AlreadyExistsException("Email already exists!");
        }

        if (teacherProfileRepository.existsTeacherProfileByTeacherCode(request.getTeacherCode())) {
            throw new AlreadyExistsException("Teacher already exists!");
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new NotFoundException("Department not found!"));

        String email = GenerateSchoolEmail.generateSchoolEmail(request.getFullName(), request.getTeacherCode(), Role.ROLE_TEACHER);

        String rawPassword = PasswordGenerater.generate(12);
        System.out.println("PASSWORD: " + rawPassword);

        User newUser = new User();

        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(rawPassword));
        newUser.setFullName(request.getFullName());
        newUser.setNormalizeName(Helper.normalizeFullName(request.getFullName()));
        newUser.setPhone(request.getPhone());
        newUser.setPersonalEmail(request.getPersonalEmail());
        newUser.setRole(Role.ROLE_TEACHER);
        newUser.setAddress(request.getAddress());

        if (request.getAvatarUrl() != null) {
            newUser.setAvatarUrl(request.getAvatarUrl());
        }

        userRepository.save(newUser);

        TeacherProfile teacher = new TeacherProfile();


        teacher.setUser(newUser);
        teacher.setTeacherCode(request.getTeacherCode());
        teacher.setDepartment(department);
        teacher.setDegree(request.getDegree());
        teacher.setJoinedDate(LocalDate.now());

        teacherProfileRepository.save(teacher);

        mailService.sendEmail(
                request.getPersonalEmail(),
                "Cấp tài khoản cho hệ thống quản lý sinh viên",
                "Email đăng nhập: " + email +
                        "\n Mật khẩu: " + rawPassword
        );

        newUser.setIsSendMail(true);

        return TeacherResponse.from(teacher);
    }

    public void importTeachers(MultipartFile file) {
        List<TeacherProfile> teachers = new ArrayList<>();
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
                String degree = Helper.getCellString(row, 5);
                String address = Helper.getCellString(row, 6);

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

                Department department = departmentRepository.findById(departmentId)
                        .orElseThrow(() -> new NotFoundException("Department not found!"));

                String teacherCode = GenerateCode.generateTeacherCode();

                String email = GenerateSchoolEmail.generateSchoolEmail(fullName, teacherCode, Role.ROLE_TEACHER);

                String rawPassword = PasswordGenerater.generate(12);
                System.out.println("PASSWORD: " + rawPassword);

                // Create user
                User user = new User();

                user.setEmail(email);
                user.setPassword(passwordEncoder.encode(rawPassword));
                user.setFullName(fullName);
                user.setNormalizeName(Helper.normalizeFullName(fullName));
                user.setPhone(phone);
                user.setRole(Role.ROLE_TEACHER);
                user.setPersonalEmail(personalEmail);
                user.setGender(gender);
                user.setAddress(address);

                users.add(user);

                // Create teacher
                TeacherProfile teacher = new TeacherProfile();

                teacher.setUser(user);
                teacher.setTeacherCode(teacherCode);
                teacher.setDegree(degree);
                teacher.setDepartment(department);
                teacher.setJoinedDate(LocalDate.now());

                teachers.add(teacher);
            }

            userRepository.saveAll(users);
            teacherProfileRepository.saveAll(teachers);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Import failed");
        }
    }

    // Get details teacher
    public TeacherResponse getDetailsTeacher(Long teacherId) {
        TeacherProfile teacher = teacherProfileRepository.findById(teacherId)
                .orElseThrow(() -> new NotFoundException("Teacher not found!"));

        return TeacherResponse.from(teacher);
    }
}
