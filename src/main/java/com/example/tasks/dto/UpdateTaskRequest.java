package com.example.tasks.dto;

import com.example.tasks.enums.TaskPriority;
import com.example.tasks.enums.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO для обновления данных задачи
 */
@Data
@Schema(description = "Запрос на обновление данных задачи")
public class UpdateTaskRequest {

    @Schema(
            description = "Новый заголовок задачи (3-200 символов)",
            example = "Обновленный заголовок задачи",
            minLength = 3,
            maxLength = 200
    )
    @Size(min = 3, max = 200, message = "Title must be 3-200 characters")
    private String title;

    @Schema(
            description = "Новое описание задачи (до 1000 символов)",
            example = "Обновленное подробное описание задачи",
            maxLength = 1000
    )
    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;

    @Schema(
            description = "Новый статус задачи",
            example = "IN_PROGRESS",
            allowableValues = {"PENDING", "IN_PROGRESS", "COMPLETED"}
    )
    private TaskStatus status;

    @Schema(
            description = "Новый приоритет задачи",
            example = "HIGH",
            allowableValues = {"HIGH", "MEDIUM", "LOW"}
    )
    private TaskPriority priority;

    @Schema(
            description = "ID нового исполнителя задачи",
            example = "456",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Assignee ID cannot be null")
    private Long assigneeId;
}