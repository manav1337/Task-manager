package com.example.taskmanager.controller;

import com.example.taskmanager.dto.request.CreateTaskRequest;
import com.example.taskmanager.dto.request.UpdateTaskRequest;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.User;
import com.example.taskmanager.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import com.example.taskmanager.repository.UserRepository;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserRepository userRepository;

    // GET ALL TASKS FOR CURRENT USER
    @GetMapping
    public ResponseEntity<List<Task>> getUserTasks(Authentication authentication) {
        String username = authentication.getName();

        try {
            User currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        log.warn("User not found: {}", username);
                        return new RuntimeException("User not found: " + username);
                    });

            List<Task> tasks = taskService.getUserTasks(currentUser.getId());
            log.info("Retrieved {} tasks for user: {}", tasks.size(), username);

            return ResponseEntity.ok(tasks);

        } catch (Exception e) {
            log.error("Error fetching tasks for user: {}", username, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET ALL TASKS (ADMIN ONLY)
    @GetMapping("/all")
    public ResponseEntity<List<Task>> getAllTasks(Authentication authentication) {
        try {
            // Check if user is admin using authorities
            boolean isAdmin = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(role -> role.equals("ROLE_ADMIN"));

            if (!isAdmin) {
                log.warn("Non-admin user attempted to access all tasks: {}", authentication.getName());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            List<Task> tasks = taskService.getAllTasks();
            log.info("Admin {} retrieved all tasks, count: {}", authentication.getName(), tasks.size());
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            log.error("Error in getAllTasks: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // CREATE NEW TASK
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody CreateTaskRequest request,
                                           Authentication authentication) {
        String username = authentication.getName();

        try {
            log.info("Creating task for user: {}, title: {}", username, request.getTitle());

            User currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        log.warn("User not found during task creation: {}", username);
                        return new RuntimeException("User not found: " + username);
                    });

            Task task = taskService.createTask(request, currentUser.getId());
            log.info("Task created successfully - ID: {}, user: {}", task.getId(), username);

            return ResponseEntity.status(HttpStatus.CREATED).body(task);

        } catch (Exception e) {
            log.error("Failed to create task for user: {}, title: {}", username, request.getTitle(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // UPDATE TASK
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable("id") Long taskId,
                                           @RequestBody UpdateTaskRequest request,
                                           Authentication authentication) {
        String username = authentication.getName();

        try {
            log.info("Updating task ID: {} for user: {}", taskId, username);

            User currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        log.warn("User not found during task update: {}", username);
                        return new RuntimeException("User not found: " + username);
                    });

            Task updatedTask = taskService.updateTask(taskId, request, currentUser.getId());
            log.info("Task updated successfully - ID: {}, user: {}", taskId, username);

            return ResponseEntity.ok(updatedTask);

        } catch (Exception e) {
            log.error("Failed to update task ID: {} for user: {}", taskId, username, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();

        try {
            log.info("Deleting task ID: {} for user: {}", id, username);

            User currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        log.warn("User not found during task deletion: {}", username);
                        return new RuntimeException("User not found: " + username);
                    });

            taskService.deleteTask(id, currentUser.getId());
            log.info("Task deleted successfully - ID: {}, user: {}", id, username);

            return ResponseEntity.ok().build();

        } catch (RuntimeException e) {
            log.warn("Delete failed for task ID: {}, user: {} - {}", id, username, e.getMessage());
            if (e.getMessage().contains("not authorized")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error("Unexpected error deleting task ID: {} for user: {}", id, username, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // GET TASK BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id,
                                            Authentication authentication) {
        try {
            String username = authentication.getName();

            // Get the current user from database using username
            User currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));

            Task task = taskService.getTaskById(id);

            // Check if task belongs to current user
            if (!task.getUser().getId().equals(currentUser.getId())) {
                log.warn("User {} attempted to access task {} belonging to another user", username, id);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            return ResponseEntity.ok(task);
        } catch (Exception e) {
            log.error("Error getting task by ID {}: ", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}