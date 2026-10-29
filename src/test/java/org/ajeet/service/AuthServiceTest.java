package org.ajeet.service;

import org.ajeet.dto.AuthResponse;
import org.ajeet.dto.LoginRequest;
import org.ajeet.dto.RegisterRequest;
import org.ajeet.model.Role;
import org.ajeet.model.User;
import org.ajeet.repository.UserRepository;
import org.ajeet.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_savesUserWithHashedPassword() {
        RegisterRequest request = new RegisterRequest("ajeet", "ajeet@example.com", "secret123", null);
        when(userRepository.existsByUsername("ajeet")).thenReturn(false);
        when(userRepository.existsByEmail("ajeet@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("hashed");

        authService.register(request);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_throwsConflictWhenUsernameTaken() {
        RegisterRequest request = new RegisterRequest("ajeet", "ajeet@example.com", "secret123", null);
        when(userRepository.existsByUsername("ajeet")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.register(request));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_returnsTokenWhenCredentialsAreValid() {
        User user = new User("ajeet", "ajeet@example.com", "hashed", Role.USER);
        when(userRepository.findByUsername("ajeet")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret123", "hashed")).thenReturn(true);
        when(jwtService.generateToken("ajeet", "USER")).thenReturn("jwt-token");

        AuthResponse response = authService.login(new LoginRequest("ajeet", "secret123"));

        assertEquals("jwt-token", response.token());
    }

    @Test
    void login_throwsUnauthorizedWhenPasswordIsWrong() {
        User user = new User("ajeet", "ajeet@example.com", "hashed", Role.USER);
        when(userRepository.findByUsername("ajeet")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.login(new LoginRequest("ajeet", "wrong")));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }
}
