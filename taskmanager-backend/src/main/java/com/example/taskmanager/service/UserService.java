package com.example.taskmanager.service;

import com.example.taskmanager.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();
    Optional<User> getUserById(Long userId);
    long getUserCount();
}