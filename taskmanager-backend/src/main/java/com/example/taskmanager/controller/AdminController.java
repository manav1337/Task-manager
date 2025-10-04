package com.example.taskmanager.controller;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.User;
import com.example.taskmanager.service.TaskService;
import com.example.taskmanager.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    // GET ALL USERS (Admin only)
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(Authentication authentication) {
        try {
            if (!isAdmin(authentication)) {
                log.warn("Non-admin user attempted to access all users: {}", authentication.getName());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Access denied. Admin role required."));
            }

            List<User> users = userService.getAllUsers();

            // Remove passwords from response for security
            List<Map<String, Object>> safeUsers = users.stream()
                    .map(user -> {
                        Map<String, Object> userInfo = new HashMap<>();
                        userInfo.put("id", user.getId());
                        userInfo.put("username", user.getUsername());
                        userInfo.put("email", user.getEmail());
                        userInfo.put("role", user.getRole());
                        userInfo.put("createdAt", user.getCreatedAt());
                        return userInfo;
                    })
                    .collect(Collectors.toList());

            log.info("Admin {} retrieved all users, count: {}", authentication.getName(), users.size());
            return ResponseEntity.ok(safeUsers);

        } catch (Exception e) {
            log.error("Error in getAllUsers: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET ALL TASKS WITH USER INFO (Admin only)
    @GetMapping("/tasks")
    public ResponseEntity<?> getAllTasksWithUsers(Authentication authentication) {
        try {
            if (!isAdmin(authentication)) {
                log.warn("Non-admin user attempted to access all tasks: {}", authentication.getName());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Access denied. Admin role required."));
            }

            List<Task> tasks = taskService.getAllTasks();

            List<Map<String, Object>> tasksWithUsers = tasks.stream()
                    .map(task -> {
                        Map<String, Object> taskInfo = new HashMap<>();
                        taskInfo.put("id", task.getId());
                        taskInfo.put("title", task.getTitle());
                        taskInfo.put("description", task.getDescription());
                        taskInfo.put("completed", task.isCompleted());
                        taskInfo.put("createdAt", task.getCreatedAt());
                        taskInfo.put("updatedAt", task.getUpdatedAt());

                        // SAFELY get user information with null checks
                        try {
                            User user = task.getUser();
                            if (user != null) {
                                Map<String, Object> userInfo = new HashMap<>();
                                userInfo.put("id", user.getId());
                                userInfo.put("username", user.getUsername());
                                userInfo.put("email", user.getEmail());
                                taskInfo.put("user", userInfo);
                            } else {
                                // Handle case where user is null
                                taskInfo.put("user", Map.of("error", "User not found"));
                            }
                        } catch (Exception e) {
                            // Handle lazy loading or other exceptions
                            log.warn("Error loading user for task {}: {}", task.getId(), e.getMessage());
                            taskInfo.put("user", Map.of("error", "Unable to load user data"));
                        }

                        return taskInfo;
                    })
                    .collect(Collectors.toList());

            log.info("Admin {} retrieved all tasks with user info, count: {}", authentication.getName(), tasks.size());
            return ResponseEntity.ok(tasksWithUsers);

        } catch (Exception e) {
            log.error("Error in getAllTasksWithUsers: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET TASKS BY SPECIFIC USER (Admin only)
    @GetMapping("/users/{userId}/tasks")
    public ResponseEntity<?> getUserTasks(@PathVariable Long userId, Authentication authentication) {
        try {
            if (!isAdmin(authentication)) {
                log.warn("Non-admin user attempted to access user tasks: {}", authentication.getName());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Access denied. Admin role required."));
            }

            List<Task> tasks = taskService.getUserTasks(userId);

            // Get user info
            User user = userService.getUserById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

            Map<String, Object> response = new HashMap<>();
            response.put("user", Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "email", user.getEmail(),
                    "role", user.getRole()
            ));
            response.put("tasks", tasks);
            response.put("taskCount", tasks.size());

            log.info("Admin {} retrieved tasks for user: {}, count: {}", authentication.getName(), user.getUsername(), tasks.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error in getUserTasks for user {}: ", userId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    // GET ADMIN STATISTICS
    @GetMapping("/stats")
    public ResponseEntity<?> getAdminStats(Authentication authentication) {
        try {
            if (!isAdmin(authentication)) {
                log.warn("Non-admin user attempted to access admin stats: {}", authentication.getName());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Access denied. Admin role required."));
            }

            long totalUsers = userService.getUserCount();
            long totalTasks = taskService.getTotalTaskCount(); // ✅ ADDED BACK

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalUsers", totalUsers);
            stats.put("totalTasks", totalTasks); // ✅ ADDED BACK

            log.info("Admin {} accessed system statistics", authentication.getName());
            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            log.error("Error in getAdminStats: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // FIXED: Helper method to check if user is admin - works with UserDetails
    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));
    }
}