package com.example.tasks.dto;


import com.example.tasks.enums.TaskPriority;
import com.example.tasks.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateTaskRequest {
    @NotBlank(message = "Title is mandatory")
    private String title;

    private String description;

    private TaskStatus status;

    private TaskPriority priority;

    @NotNull(message = "Assignee ID is mandatory")
    private Long assigneeId;

}
