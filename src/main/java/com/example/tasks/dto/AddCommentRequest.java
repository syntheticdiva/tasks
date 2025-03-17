package com.example.tasks.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO для добавления нового комментария
 */
@Data
@Schema(description = "Запрос на создание комментария")
public class AddCommentRequest {

    @Schema(
            description = "Текст комментария",
            example = "Необходимо проверить детали реализации",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 1,
            maxLength = 2000
    )
    @NotBlank(message = "Comment text is required")
    @Size(max = 2000, message = "Comment must be less than 2000 characters")
    private String text;
}