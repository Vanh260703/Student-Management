package com.example.quan_ly_sinh_vien_v2.Config;

import com.example.quan_ly_sinh_vien_v2.Modal.Entity.User;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.Role;
import com.example.quan_ly_sinh_vien_v2.Repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsUserByEmail("admin@edu.vn")) {
            User admin = new User();

            admin.setEmail("admin@edu.vn");
            admin.setPersonalEmail("admin@edu.vn");
            admin.setNormalizeName("admin");
            admin.setRole(Role.ROLE_ADMIN);
            admin.setFullName("ADMIN");
            admin.setPassword(passwordEncoder.encode("admin@123"));
            admin.setIsActive(true);

            userRepository.save(admin);

            System.out.println("KHỞI TẠO ADMIN THÀNH CÔNG!!!");
        } else {
            System.out.println("ADMIN ĐÃ TỒN TẠI TRONG HỆ THỐNG. KHÔNG CẦN KHỞI TẠO LẠI!!!");
        }
    }
}
