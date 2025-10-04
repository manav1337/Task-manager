package com.example.taskmanager.service.impl;

import com.example.taskmanager.dto.request.LoginRequest;
import com.example.taskmanager.dto.request.RegisterRequest;
import com.example.taskmanager.dto.response.AuthResponse;
import com.example.taskmanager.dto.response.ApiResponse;
import com.example.taskmanager.model.User;
import com.example.taskmanager.model.Role;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.service.AuthService;
import com.example.taskmanager.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        String username = loginRequest.getUsername();

        try {
            // Check if user exists first
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        log.warn("Login failed - user not found: {}", username);
                        return new RuntimeException("User not found");
                    });

            // Attempt authentication
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT token
            String jwtToken = jwtUtil.generateJwtToken(authentication);

            AuthResponse response = new AuthResponse();
            response.setId(user.getId());
            response.setUsername(user.getUsername());
            response.setEmail(user.getEmail());
            response.setRole(user.getRole());
            response.setToken(jwtToken);
            response.setType("Bearer");

            log.info("Login successful for user: {}", username);
            return response;

        } catch (Exception e) {
            log.error("Login failed for user: {}", username);
            throw new RuntimeException("Invalid username or password");
        }
    }

    @Override
    public ApiResponse register(RegisterRequest registerRequest) {
        String username = registerRequest.getUsername();
        String email = registerRequest.getEmail();

        try {
            if (userRepository.existsByUsername(username)) {
                log.warn("Registration failed - username already taken: {}", username);
                return new ApiResponse(false, "Username is already taken!");
            }

            if (userRepository.existsByEmail(email)) {
                log.warn("Registration failed - email already registered: {}", email);
                return new ApiResponse(false, "Email is already registered!");
            }

            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setRole(Role.ROLE_USER);

            userRepository.save(user);

            log.info("User registered successfully: {}", username);
            return new ApiResponse(true, "User registered successfully!");

        } catch (Exception e) {
            log.error("Registration failed for user: {}", username, e);
            return new ApiResponse(false, "Registration failed due to an unexpected error");
        }
    }
}