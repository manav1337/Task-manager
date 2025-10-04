package com.example.taskmanager.service;

import com.example.taskmanager.dto.request.CreateTaskRequest;
import com.example.taskmanager.dto.request.UpdateTaskRequest;
import com.example.taskmanager.model.Task;
import java.util.List;

public interface TaskService {
    // User operations
    Task createTask(CreateTaskRequest request, Long userId);
    List<Task> getUserTasks(Long userId);
    Task updateTask(Long taskId, UpdateTaskRequest request, Long userId);
    void deleteTask(Long taskId, Long userId);

    // Admin operations
    List<Task> getAllTasks();
    Task getTaskById(Long taskId);
}
