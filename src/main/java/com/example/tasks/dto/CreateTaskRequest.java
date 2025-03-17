package com.example.tasks.dto;


import com.example.tasks.enums.TaskPriority;
import com.example.tasks.enums.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO для создания новой задачи
 */
@Data
@Schema(description = "Запрос на создание задачи")
public class CreateTaskRequest {

    @Schema(
            description = "Название задачи",
            example = "Реализовать задачу",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 255
    )
    @NotBlank(message = "Title is mandatory")
    private String title;

    @Schema(
            description = "Подробное описание задачи",
            example = "Необходимо добавить новую фуункцию"
    )
    private String description;

    @Schema(
            description = "Статус задачи",
            example = "PENDING",
            allowableValues = {"PENDING", "IN_PROGRESS", "COMPLETED"}
    )
    private TaskStatus status;

    @Schema(
            description = "Приоритет выполнения",
            example = "MEDIUM",
            allowableValues = {"HIGH", "MEDIUM", "LOW"}
    )
    private TaskPriority priority;

    @Schema(
            description = "ID назначенного исполнителя",
            example = "456",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Assignee ID is mandatory")
    private Long assigneeId;
}