package com.example.tasks.dto;

import com.example.tasks.enums.TaskPriority;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdatePriorityRequest {
    @NotNull(message = "Priority is required")
    private TaskPriority priority;
}