package com.example.tasks.controller;


import com.example.tasks.dto.*;
import com.example.tasks.entity.User;
import com.example.tasks.enums.TaskPriority;
import com.example.tasks.enums.TaskStatus;
import com.example.tasks.exception.TaskNotFoundException;
import com.example.tasks.exception.UserNotFoundException;
import com.example.tasks.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Контроллер для управления задачами.
 * <p>
 * Обеспечивает REST API для операций CRUD с задачами, управления комментариями,
 * фильтрации и назначения задач. Интегрирован с Spring Security и JWT аутентификацией.
 * Документирован с использованием Swagger/OpenAPI.
 * </p>
 *
 * @author AlinaSheveleva
 * @version 1.0
 */
@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Task Management", description = "API для управления задачами")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class TaskController {
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    private final TaskService taskService;

    /**
     * Получает задачи по ID автора.
     *
     * @param authorId ID автора (>= 1)
     * @return список DTO задач
     * */
    @Operation(
            summary = "Получить задачи по автору",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Задачи успешно найдены"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Автор не найден")
            }
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<TaskDTO>> getTasksByAuthor(
            @Parameter(description = "ID автора задачи") @PathVariable Long authorId) {
        List<TaskDTO> tasks = taskService.getTasksByAuthor(authorId);
        return ResponseEntity.ok(tasks);
    }

    @Operation(
            summary = "Получить задачи по исполнителю",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Задачи успешно найдены"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Исполнитель не найден")
            }
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/assignee/{assigneeId}")
    public ResponseEntity<List<TaskDTO>> getTasksByAssignee(
            @PathVariable Long assigneeId) {
        List<TaskDTO> tasks = taskService.getTasksByAssignee(assigneeId);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Создает новую задачу.
     *
     * @param request DTO с данными задачи
     * @param authentication данные аутентификации
     * @return созданная задача (201 Created)
     */
    @Operation(
            summary = "Создать новую задачу",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Задача успешно создана"),
                    @ApiResponse(responseCode = "400", description = "Некорректные входные данные"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<TaskDTO> createTask(
            @Valid @RequestBody CreateTaskRequest request,
            Authentication authentication) {
        User author = (User) authentication.getPrincipal();
        TaskDTO taskDTO = taskService.createTask(
                request.getTitle(),
                request.getDescription(),
                request.getStatus(),
                request.getPriority(),
                author,
                request.getAssigneeId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(taskDTO);
    }

    /**
     * Обновляет существующую задачу.
     *
     * @param taskId ID обновляемой задачи
     * @param request DTO с обновляемыми полями
     * @return обновленная задача
     */
    @Operation(
            summary = "Обновить задачу",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Задача успешно обновлена"),
                    @ApiResponse(responseCode = "400", description = "Некорректные входные данные"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Задача не найдена")
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskDTO> updateTask(
            @Parameter(description = "ID задачи") @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskRequest request) {
        TaskDTO taskDTO = taskService.updateTask(
                taskId,
                request.getTitle(),
                request.getDescription(),
                request.getStatus(),
                request.getPriority(),
                request.getAssigneeId()
        );
        return ResponseEntity.ok(taskDTO);
    }

    /**
     * Добавляет комментарий к задаче.
     *
     * @param taskId ID задачи
     * @param request DTO с текстом комментария
     * @param authentication данные аутентификации
     * @return созданный комментарий (201 Created)
     */
    @Operation(
            summary = "Добавить комментарий к задаче",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Комментарий успешно добавлен"),
                    @ApiResponse(responseCode = "400", description = "Некорректные входные данные"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Задача не найдена")
            }
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/{taskId}/comments")
    public ResponseEntity<CommentDTO> addComment(
            @Parameter(description = "ID задачи") @PathVariable Long taskId,
            @Valid @RequestBody AddCommentRequest request,
            Authentication authentication) {
        User author = (User) authentication.getPrincipal();
        CommentDTO commentDTO = taskService.addComment(taskId, request.getText(), author);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentDTO);
    }

    @Operation(
            summary = "Изменить приоритет задачи",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Приоритет задачи успешно обновлен"),
                    @ApiResponse(responseCode = "400", description = "Некорректные входные данные"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Задача не найдена")
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{taskId}/priority")
    public ResponseEntity<TaskDTO> updateTaskPriority(
            @Parameter(description = "ID задачи") @PathVariable Long taskId,
            @Valid @RequestBody UpdatePriorityRequest request) {
        TaskDTO taskDTO = taskService.updateTaskPriority(taskId, request.getPriority());
        return ResponseEntity.ok(taskDTO);
    }

    @Operation(
            summary = "Обновить статус задачи",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Статус задачи успешно обновлен"),
                    @ApiResponse(responseCode = "400", description = "Некорректные входные данные"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Задача не найдена")
            }
    )
    @PutMapping("/{taskId}/status")
    public ResponseEntity<TaskDTO> updateTaskStatus(
            @Parameter(description = "ID задачи") @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskStatusRequest request,
            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        TaskDTO taskDTO = taskService.updateTaskStatus(taskId, request.getStatus(), currentUser);
        return ResponseEntity.ok(taskDTO);
    }

    /**
     * Удаляет задачу
     * @param taskId ID удаляемой задачи
     * @return HTTP 204 при успешном удалении
     */
    @Operation(
            summary = "Удалить задачу",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Задача успешно удалена"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Задача не найдена")
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @Parameter(description = "ID задачи") @PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Назначает задачу исполнителю.
     *
     * @param taskId ID задачи
     * @param assigneeId ID нового исполнителя
     * @return обновленная задача
     */
    @Operation(
            summary = "Назначить задачу исполнителю",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Задача успешно назначена"),
                    @ApiResponse(responseCode = "400", description = "Некорректные входные данные"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Задача или исполнитель не найдены")
            }
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/{taskId}/assign/{assigneeId}")
    public ResponseEntity<TaskDTO> assignTask(
            @Parameter(description = "ID задачи") @PathVariable Long taskId,
            @Parameter(description = "ID исполнителя") @PathVariable Long assigneeId) {

        logger.info("Assigning task ID {} to assignee ID {}", taskId, assigneeId);

        try {
            TaskDTO taskDTO = taskService.assignTask(taskId, assigneeId);
            return ResponseEntity.ok(taskDTO);
        } catch (TaskNotFoundException | UserNotFoundException ex) {
            logger.error("Assignment error: {}", ex.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        }
    }

    /**
     * Получает задачи с фильтрацией и пагинацией.
     *
     * @param status фильтр по статусу
     * @param priority фильтр по приоритету
     * @param authorId фильтр по автору
     * @param assigneeId фильтр по исполнителю
     * @param page номер страницы (0-based)
     * @param size размер страницы (1-100)
     * @return страница с результатами
     */
    @Operation(
            summary = "Получить задачи с фильтрацией",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Задачи успешно найдены"),
                    @ApiResponse(responseCode = "400", description = "Некорректные параметры фильтрации"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
            }
    )
    @GetMapping
    public ResponseEntity<Page<TaskDTO>> getTasks(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<TaskDTO> tasks = taskService.getTasks(status, priority, authorId, assigneeId, page, size);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Получает все задачи (администраторский доступ)
     * @param page номер страницы (по умолчанию: 0)
     * @param size размер страницы (по умолчанию: 20, максимум: 100)
     * @return страница со всеми задачами и HTTP 200
     */
    @Operation(
            summary = "Получить все задачи (только для админов)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Задачи успешно найдены"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/all")
    public ResponseEntity<Page<TaskDTO>> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<TaskDTO> tasks = taskService.getAllTasks(page, size);
        return ResponseEntity.ok(tasks);
    }
}