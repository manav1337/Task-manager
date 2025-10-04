package com.example.taskmanager.dto.request;

import lombok.Data;

@Data
public class UpdateTaskRequest {
    private String title;
    private String description;
    private Boolean completed;
}