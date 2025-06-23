package com.example.login.service;
import com.example.login.model.User;
import com.example.login.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister_shouldEncodePasswordAndSaveUser() {
        String username = "test";
        String rawPassword = "123";
        String encodedPassword = "encoded123";

        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User savedUser = userService.register(username, rawPassword);

        assertEquals(username, savedUser.getUsername());
        assertEquals(encodedPassword, savedUser.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testLogin_shouldReturnUserIfPasswordMatches() {
        String username = "test";
        String rawPassword = "123";
        String encodedPassword = "encoded123";

        User user = new User(1L, username, encodedPassword);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        User result = userService.login(username, rawPassword);

        assertEquals(username, result.getUsername());
        verify(userRepository).findByUsername(username);
    }

    @Test
    void testLogin_shouldThrowIfUserNotFound() {
        when(userRepository.findByUsername("notfound")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.login("notfound", "password");
        });
    }

    @Test
    void testLogin_shouldThrowIfPasswordWrong() {
        String username = "test";
        String rawPassword = "wrong";
        String encodedPassword = "encoded";

        User user = new User(1L, username, encodedPassword);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> {
            userService.login(username, rawPassword);
        });
    }
}

