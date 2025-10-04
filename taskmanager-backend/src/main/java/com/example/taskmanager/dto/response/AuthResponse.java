package com.example.taskmanager.dto.response;

import com.example.taskmanager.model.Role;
import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private Role role;
}
