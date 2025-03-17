package com.example.tasks.service;


import com.example.tasks.dto.CommentDTO;
import com.example.tasks.dto.TaskDTO;
import com.example.tasks.entity.Comment;
import com.example.tasks.entity.Task;
import com.example.tasks.entity.User;
import com.example.tasks.enums.TaskPriority;
import com.example.tasks.enums.TaskStatus;
import com.example.tasks.exception.InvalidRequestException;
import com.example.tasks.exception.TaskNotFoundException;
import com.example.tasks.exception.UnauthorizedActionException;
import com.example.tasks.exception.UserNotFoundException;
import com.example.tasks.mapper.CommentMapper;
import com.example.tasks.mapper.TaskMapper;
import com.example.tasks.repository.CommentRepository;
import com.example.tasks.repository.TaskRepository;
import com.example.tasks.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для управления задачами и связанными сущностями.
 * <p>
 * Обеспечивает бизнес-логику для операций CRUD с задачами, обработку комментариев,
 * фильтрацию и пагинацию. Интегрируется с системой безопасности для проверки прав доступа.
 * </p>
 *
 * @author AlinaSheveleva
 * @version 1.0
 */
@Service
@Validated
public class TaskService {
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    private static final int MIN_ID_VALUE = 1;
    private static final int MAX_PAGE_SIZE = 100;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final TaskMapper taskMapper;
    private final CommentMapper commentMapper;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository, CommentRepository commentRepository, TaskMapper taskMapper, CommentMapper commentMapper) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.taskMapper = taskMapper;
        this.commentMapper = commentMapper;
    }
    /**
     * Получает список задач по ID автора.
     *
     * @param authorId ID автора задач (должно быть >= 1)
     * @return список DTO задач
     * @throws UserNotFoundException если автор не найден
     */
    public List<TaskDTO> getTasksByAuthor(@NotNull @Min(MIN_ID_VALUE) Long authorId) {
        logger.info("Attempting to find tasks for author ID: {}", authorId);

        if (!userRepository.existsById(authorId)) {
            logger.error("Author with ID {} not found", authorId);
            throw new UserNotFoundException("Автор с ID " + authorId + " не найден");
        }

        List<Task> tasks = taskRepository.findByAuthorId(authorId);
        logger.debug("Found {} tasks for author ID: {}", tasks.size(), authorId);

        return tasks.stream()
                .map(taskMapper::toTaskDTO)
                .collect(Collectors.toList());
    }

    /**
     * Получает список задач по ID исполнителя.
     *
     * @param assigneeId ID исполнителя (должно быть >= 1)
     * @return список DTO задач
     * @throws UserNotFoundException если исполнитель не найден
     */
    public List<TaskDTO> getTasksByAssignee(Long assigneeId) {
        if (!userRepository.existsById(assigneeId)) {
            throw new UserNotFoundException("Исполнитель с ID " + assigneeId + " не найден");
        }

        return taskRepository.findByAssigneeId(assigneeId)
                .stream()
                .map(taskMapper::toTaskDTO)
                .collect(Collectors.toList());
    }
    /**
     * Создает новую задачу
     *
     * @param title название задачи (обязательно)
     * @param description описание задачи (обязательно)
     * @param status статус задачи (по умолчанию PENDING)
     * @param priority приоритет задачи (по умолчанию MEDIUM)
     * @param author автор задачи (обязательно)
     * @param assigneeId ID исполнителя (обязательно)
     * @return созданная задача в формате DTO
     * @throws UserNotFoundException если исполнитель не найден
     */
    @Transactional
    public TaskDTO createTask(
            @NotNull String title,
            @NotNull String description,
            TaskStatus status,
            TaskPriority priority,
            @NotNull User author,
            @NotNull Long assigneeId
    ) {
        logger.info("Creating task with title: {}", title);
        User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new UserNotFoundException("Assignee not found with id: " + assigneeId));

        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(status != null ? status : TaskStatus.PENDING);
        task.setPriority(priority != null ? priority : TaskPriority.MEDIUM);
        task.setAuthor(author);
        task.setAssignee(assignee);

        Task savedTask = taskRepository.save(task);
        return taskMapper.toTaskDTO(savedTask);
    }
    /**
     * Обновляет существующую задачу.
     *
     * @param taskId ID задачи для обновления
     * @param title новое название (если не null)
     * @param description новое описание (если не null)
     * @param status новый статус (если не null)
     * @param priority новый приоритет (если не null)
     * @param assigneeId новый ID исполнителя (если не null)
     * @return обновленная задача в формате DTO
     * @throws TaskNotFoundException если задача не найдена
     * @throws UserNotFoundException если новый исполнитель не найден
     */
    public TaskDTO updateTask(
            @NotNull Long taskId,
            String title,
            String description,
            TaskStatus status,
            TaskPriority priority,
            Long assigneeId
    ) {
        logger.info("Updating task with ID: {}", taskId);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + taskId));

        if (title != null) {
            task.setTitle(title);
        }
        if (description != null) {
            task.setDescription(description);
        }
        if (status != null) {
            task.setStatus(status);
        }
        if (priority != null) {
            task.setPriority(priority);
        }
        if (assigneeId != null) {
            User assignee = userRepository.findById(assigneeId)
                    .orElseThrow(() -> new UserNotFoundException("Assignee not found with id: " + assigneeId));
            task.setAssignee(assignee);
        }

        Task updatedTask = taskRepository.save(task);
        return taskMapper.toTaskDTO(updatedTask);
    }

    /**
     * Удаляет задачу по ID.
     *
     * @param taskId ID задачи для удаления (не может быть null)
     * @throws TaskNotFoundException если задача не найдена
     */
    public void deleteTask(@NotNull Long taskId) {
        logger.info("Deleting task with ID: {}", taskId);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + taskId));
        taskRepository.delete(task);
    }

    /**
     * Назначает задачу исполнителю.
     *
     * @param taskId ID задачи (не null)
     * @param assigneeId ID исполнителя (не null)
     * @return обновленная задача в формате DTO
     * @throws TaskNotFoundException если задача не найдена
     * @throws UserNotFoundException если исполнитель не найден
     */
    public TaskDTO assignTask(
            @NotNull(message = "Task ID cannot be null") Long taskId,
            @NotNull(message = "Assignee ID cannot be null") Long assigneeId) {

        logger.info("Assigning task with ID: {} to assignee with ID: {}", taskId, assigneeId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    logger.error("Task not found with ID: {}", taskId);
                    return new TaskNotFoundException("Task not found with id: " + taskId);
                });

        User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> {
                    logger.error("Assignee not found with ID: {}", assigneeId);
                    return new UserNotFoundException("Assignee not found with id: " + assigneeId);
                });

        task.setAssignee(assignee);
        Task assignedTask = taskRepository.save(task);

        logger.info("Task ID: {} successfully assigned to user ID: {}", taskId, assigneeId);
        return taskMapper.toTaskDTO(assignedTask);
    }
    /**
     * Получает задачи с фильтрацией и пагинацией.
     *
     * @param status фильтр по статусу
     * @param priority фильтр по приоритету
     * @param authorId фильтр по ID автора
     * @param assigneeId фильтр по ID исполнителя
     * @param page номер страницы (>= 0)
     * @param size размер страницы (1-100)
     * @return страница с DTO задач
     * @throws InvalidRequestException при невалидных параметрах пагинации
     * @throws TaskNotFoundException если задачи не найдены
     */
    public Page<TaskDTO> getTasks(
            TaskStatus status,
            TaskPriority priority,
            Long authorId,
            Long assigneeId,
            int page,
            int size
    ) {
        logger.info("Fetching tasks with filters: status={}, priority={}, authorId={}, assigneeId={}, page={}, size={}",
                status, priority, authorId, assigneeId, page, size);

        validatePageAndSize(page, size);

        if (status == null && priority == null && authorId == null && assigneeId == null) {
            throw new InvalidRequestException("At least one filter parameter must be provided");
        }

        Specification<Task> spec = Specification.where(null);

        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (priority != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("priority"), priority));
        }
        if (authorId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("author").get("id"), authorId));
        }
        if (assigneeId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("assignee").get("id"), assigneeId));
        }

        Page<Task> tasks = taskRepository.findAll(spec, PageRequest.of(page, size));

        if (tasks.isEmpty()) {
            throw new TaskNotFoundException("No tasks found with the specified filters");
        }

        return tasks.map(taskMapper::toTaskDTO);
    }
    /**
     * Добавляет комментарий к задаче.
     *
     * @param taskId ID задачи
     * @param text текст комментария
     * @param author автор комментария
     * @return созданный комментарий в формате DTO
     * @throws TaskNotFoundException если задача не найдена
     * @throws UnauthorizedActionException если пользователь не имеет прав на комментарий
     */
    public CommentDTO addComment(@NotNull Long taskId, @NotNull String text, @NotNull User author) {
        logger.info("Adding comment to task with ID: {}", taskId);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + taskId));

        if (!task.getAuthor().getId().equals(author.getId())
                && !task.getAssignee().getId().equals(author.getId())) {
            throw new UnauthorizedActionException("You are not authorized to comment on this task");
        }

        Comment comment = new Comment();
        comment.setText(text);
        comment.setTask(task);
        comment.setAuthor(author);

        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toCommentDTO(savedComment);
    }
    /**
     * Обновляет приоритет задачи.
     *
     * @param taskId ID задачи для обновления (не может быть null)
     * @param priority новый приоритет задачи (не может быть null)
     * @return обновленная задача в формате DTO
     * @throws TaskNotFoundException если задача не найдена
     */
    @Transactional
    public TaskDTO updateTaskPriority(@NotNull Long taskId, @NotNull TaskPriority priority) {
        logger.info("Updating priority of task with ID: {}", taskId);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + taskId));

        task.setPriority(priority);
        Task updatedTask = taskRepository.save(task);

        return taskMapper.toTaskDTO(updatedTask);
    }
    /**
     * Обновляет статус задачи с проверкой прав доступа.
     *
     * @param taskId ID задачи
     * @param status новый статус
     * @param currentUser текущий пользователь
     * @return обновленная задача в формате DTO
     * @throws TaskNotFoundException если задача не найдена
     * @throws AccessDeniedException если пользователь не имеет прав на изменение
     */
    public TaskDTO updateTaskStatus(@NotNull Long taskId, @NotNull TaskStatus status, @NotNull User currentUser) {
        logger.info("Updating status of task with ID: {}", taskId);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + taskId));

        if (currentUser.hasUserRole() &&
                !task.getAssignee().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("No permission to update status");
        }

        task.setStatus(status);
        Task updatedTask = taskRepository.save(task);

        return taskMapper.toTaskDTO(updatedTask);
    }

    /**
     * Получает все задачи с пагинацией (только для администраторов).
     *
     * @param page номер страницы (>= 0)
     * @param size размер страницы (1-100)
     * @return страница с DTO задач
     */
    public Page<TaskDTO> getAllTasks(int page, int size) {
        logger.info("Fetching all tasks with page={}, size={}", page, size);
        validatePageAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Task> tasks = taskRepository.findAll(pageable);

        return tasks.map(taskMapper::toTaskDTO);
    }

    /**
     * Валидирует параметры пагинации.
     *
     * @param page номер страницы
     * @param size размер страницы
     * @throws InvalidRequestException если параметры не соответствуют ограничениям
     */
    private void validatePageAndSize(int page, int size) {
        if (page < 0) {
            throw new InvalidRequestException("Page number must not be less than zero");
        }

        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new InvalidRequestException("Page size must be between 1 and 100");
        }
    }
}
