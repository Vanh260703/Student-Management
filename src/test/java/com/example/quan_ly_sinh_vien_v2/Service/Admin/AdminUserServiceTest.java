package com.example.quan_ly_sinh_vien_v2.Service.Admin;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.Admin.UpdateUserRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.UserResponse;
import com.example.quan_ly_sinh_vien_v2.Exception.UpdateFailException;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.User;
import com.example.quan_ly_sinh_vien_v2.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {
    @Mock
    private UserRepository userRepository;

    private AdminUserService adminUserService;

    @BeforeEach
    void setUp() {
        adminUserService = new AdminUserService(userRepository);
    }

    @Test
    void updateUserShouldPersistNormalizedNameAndPhone() {
        User user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        user.setFullName("Old Name");
        user.setPhone("0900000000");

        UpdateUserRequest request = new UpdateUserRequest();
        ReflectionTestUtils.setField(request, "fullName", "Nguyen Van A");
        ReflectionTestUtils.setField(request, "phone", "0911222333");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = adminUserService.updateUser(1L, request);

        verify(userRepository).save(user);
        assertEquals("Nguyen Van A", user.getFullName());
        assertEquals("nguyenvana", user.getNormalizeName());
        assertEquals("0911222333", response.getPhone());
    }

    @Test
    void updateUserShouldRejectWhenRequestHasNoUpdatableField() {
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UpdateUserRequest request = new UpdateUserRequest();

        assertThrows(UpdateFailException.class, () -> adminUserService.updateUser(1L, request));
    }
}
