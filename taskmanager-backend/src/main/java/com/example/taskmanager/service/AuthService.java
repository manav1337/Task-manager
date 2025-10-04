package com.example.taskmanager.service;

import com.example.taskmanager.dto.request.LoginRequest;
import com.example.taskmanager.dto.request.RegisterRequest;
import com.example.taskmanager.dto.response.AuthResponse;
import com.example.taskmanager.dto.response.ApiResponse;

public interface AuthService {
    AuthResponse login(LoginRequest loginRequest);
    ApiResponse register(RegisterRequest registerRequest);
}
