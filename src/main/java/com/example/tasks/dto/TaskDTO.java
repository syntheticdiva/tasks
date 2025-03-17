package com.example.tasks.dto;

import com.example.tasks.enums.TaskPriority;
import com.example.tasks.enums.TaskStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * DTO для представления задачи
 */
@Data
@Schema(description = "Детализированное представление задачи с комментариями")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskDTO {

    @Schema(
            description = "Уникальный идентификатор задачи",
            example = "123",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Schema(
            description = "Название задачи",
            example = "Реализация задачи",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 255
    )
    private String title;

    @Schema(
            description = "Подробное описание задачи",
            example = "Необходимо добавить новую функцию"
    )
    private String description;

    @Schema(
            description = "Текущий статус задачи",
            example = "IN_PROGRESS",
            allowableValues = {"PENDING", "IN_PROGRESS", "COMPLETED"}
    )
    private TaskStatus status;

    @Schema(
            description = "Приоритет выполнения",
            example = "HIGH",
            allowableValues = {"HIGH", "MEDIUM", "LOW"}
    )
    private TaskPriority priority;

    @Schema(
            description = "ID автора задачи",
            example = "456",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long authorId;

    @Schema(
            description = "ID назначенного исполнителя",
            example = "789"
    )
    private Long assigneeId;

    @ArraySchema(
            arraySchema = @Schema(
                    description = "Список комментариев к задаче"
            ),
            schema = @Schema(implementation = CommentDTO.class)
    )
    private List<CommentDTO> comments;
}