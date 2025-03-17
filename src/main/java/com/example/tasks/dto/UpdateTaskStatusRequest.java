package com.example.tasks.dto;

import com.example.tasks.enums.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO для обновления статуса задачи
 */
@Data
@Schema(description = "Запрос на изменение статуса задачи")
public class UpdateTaskStatusRequest {

    @Schema(
            description = "Новый статус задачи",
            example = "IN_PROGRESS",
            allowableValues = {"PENDING", "IN_PROGRESS", "COMPLETED"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Status is required")
    private TaskStatus status;
}