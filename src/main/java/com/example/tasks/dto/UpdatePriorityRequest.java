package com.example.tasks.dto;

import com.example.tasks.enums.TaskPriority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO для обновления приоритета задачи
 */
@Data
@Schema(description = "Запрос на изменение приоритета задачи")
public class UpdatePriorityRequest {

    @Schema(
            description = "Новый приоритет задачи",
            example = "HIGH",
            allowableValues = {"HIGH", "MEDIUM", "LOW"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Priority is required")
    private TaskPriority priority;
}