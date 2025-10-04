package com.example.taskmanager.service.impl;

import com.example.taskmanager.dto.request.CreateTaskRequest;
import com.example.taskmanager.dto.request.UpdateTaskRequest;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Task createTask(CreateTaskRequest request, Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.warn("User not found when creating task: {}", userId);
                        return new RuntimeException("User not found with id: " + userId);
                    });

            Task task = new Task();
            task.setTitle(request.getTitle());
            task.setDescription(request.getDescription());
            task.setCompleted(false);
            task.setUser(user);

            Task savedTask = taskRepository.save(task);
            log.info("Task created - ID: {}, user: {}, title: {}", savedTask.getId(), userId, request.getTitle());

            return savedTask;

        } catch (Exception e) {
            log.error("Failed to create task for user: {}, title: {}", userId, request.getTitle(), e);
            throw new RuntimeException("Failed to create task: " + e.getMessage());
        }
    }

    @Override
    public List<Task> getUserTasks(Long userId) {
        return taskRepository.findByUserId(userId);
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public Task getTaskById(Long taskId) {
        try {
            return taskRepository.findById(taskId)
                    .orElseThrow(() -> {
                        log.warn("Task not found: {}", taskId);
                        return new RuntimeException("Task not found with id: " + taskId);
                    });
        } catch (Exception e) {
            log.error("Error getting task by ID: {}", taskId, e);
            throw e;
        }
    }

    @Override
    public Task updateTask(Long taskId, UpdateTaskRequest request, Long userId) {
        try {
            Task task = taskRepository.findById(taskId)
                    .orElseThrow(() -> {
                        log.warn("Task not found during update: {}", taskId);
                        return new RuntimeException("Task not found with id: " + taskId);
                    });

            if (!task.getUser().getId().equals(userId)) {
                log.warn("Unauthorized update attempt - task: {}, user: {}", taskId, userId);
                throw new RuntimeException("You are not authorized to update this task");
            }

            if (request.getTitle() != null) {
                task.setTitle(request.getTitle());
            }
            if (request.getDescription() != null) {
                task.setDescription(request.getDescription());
            }
            if (request.getCompleted() != null) {
                task.setCompleted(request.getCompleted());
            }

            Task updatedTask = taskRepository.save(task);
            log.info("Task updated - ID: {}, user: {}", taskId, userId);

            return updatedTask;

        } catch (Exception e) {
            log.error("Failed to update task: {}, user: {}", taskId, userId, e);
            throw e;
        }
    }

    @Override
    public void deleteTask(Long taskId, Long userId) {
        try {
            Task task = taskRepository.findById(taskId)
                    .orElseThrow(() -> {
                        log.warn("Task not found during deletion: {}", taskId);
                        return new RuntimeException("Task not found with id: " + taskId);
                    });

            if (!task.getUser().getId().equals(userId)) {
                log.warn("Unauthorized delete attempt - task: {}, user: {}", taskId, userId);
                throw new RuntimeException("You are not authorized to delete this task");
            }

            taskRepository.delete(task);
            log.info("Task deleted - ID: {}, user: {}", taskId, userId);

        } catch (Exception e) {
            log.error("Failed to delete task: {}, user: {}", taskId, userId, e);
            throw e;
        }
    }
}