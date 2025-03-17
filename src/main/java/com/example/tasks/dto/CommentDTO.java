package com.example.tasks.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO для представления комментария
 */
@Data
@Schema(description = "Комментарий к задаче")
public class CommentDTO {

    @Schema(
            description = "Уникальный идентификатор комментария",
            example = "456",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Schema(
            description = "Текст комментария",
            example = "Комментарий к задаче",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 2000
    )
    @NotBlank(message = "Text is required")
    @Size(max = 2000, message = "Comment must be less than 2000 characters")
    private String text;

    @Schema(
            description = "ID связанной задачи",
            example = "123",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Task ID is required")
    private Long taskId;

    @Schema(
            description = "ID автора комментария",
            example = "789",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Author ID is required")
    private Long authorId;
}