package com.example.taskmanager.dto.response;

import lombok.Data;

@Data
public class ApiResponse {
    private Boolean success;
    private String message;

    public ApiResponse(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public Boolean isSuccess() {
        return success;
    }

    // No-args constructor (for JSON serialization)
    public ApiResponse() {}
}
