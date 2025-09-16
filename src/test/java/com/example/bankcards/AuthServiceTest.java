package com.example.bankcards;

import com.example.bankcards.dto.auth.AuthCommand;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.EntityNotFoundException;
import com.example.bankcards.exception.InvalidDataException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void authenticate_Success() {
        User user = User.builder()
                .username("testuser")
                .password("encodedPassword")
                .build();

        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);

        User result = authService.authenticate("testuser", "password");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void authenticate_UserNotFound_ThrowsException() {
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                authService.authenticate("testuser", "password")
        );
    }

    @Test
    void authenticate_WrongPassword_ThrowsException() {
        User user = User.builder()
                .username("testuser")
                .password("encodedPassword")
                .build();

        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        assertThrows(InvalidDataException.class, () ->
                authService.authenticate("testuser", "wrongpassword")
        );
    }

    @Test
    void register_Success() {
        AuthCommand command = new AuthCommand("testuser", "email@test.com", "password");

        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        User result = authService.register(command);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("email@test.com", result.getEmail());
        assertEquals("encodedPassword", result.getPassword());
        assertEquals(Role.USER, result.getRole());
    }

    @Test
    void register_UserAlreadyExists_ThrowsException() {
        AuthCommand command = new AuthCommand("testuser", "email@test.com", "password");
        User existingUser = User.builder().username("testuser").build();

        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(existingUser));

        assertThrows(InvalidDataException.class, () ->
                authService.register(command)
        );
    }
}