package com.example.tasks.mapper;

import com.example.tasks.dto.TaskDTO;
import com.example.tasks.entity.Task;
import com.example.tasks.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Маппер для преобразования между сущностью {@link Task} и DTO {@link TaskDTO}.
 * <p>
 * Использует MapStruct с интеграцией Spring ({@code componentModel = "spring"})
 * и подключает {@link CommentMapper} для обработки комментариев.
 * </p>
 */
@Mapper(componentModel = "spring", uses = {CommentMapper.class})
public abstract class TaskMapper {

    /**
     * Преобразует сущность задачи в DTO.
     * <p>
     * Особенности преобразования:
     * <ul>
     *   <li>Автор задачи → authorId (через {@link #mapAuthorId(User)})</li>
     *   <li>Исполнитель задачи → assigneeId (через {@link #mapAssigneeId(User)})</li>
     *   <li>Комментарии преобразуются с использованием {@link CommentMapper}</li>
     * </ul>
     *
     * @param task сущность задачи (не null)
     * @return DTO задачи с заполненными authorId и assigneeId
     */
    @Mapping(source = "author", target = "authorId", qualifiedByName = "mapAuthorId")
    @Mapping(source = "assignee", target = "assigneeId", qualifiedByName = "mapAssigneeId")
    public abstract TaskDTO toTaskDTO(Task task);

    /**
     * Преобразует DTO задачи в сущность.
     * <p>
     * Особенности преобразования:
     * <ul>
     *   <li>Поля author и assignee игнорируются (требуют отдельной привязки)</li>
     *   <li>Комментарии преобразуются с использованием {@link CommentMapper}</li>
     * </ul>
     *
     * @param taskDTO DTO задачи (не null)
     * @return сущность задачи без привязки автора и исполнителя
     */
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    public abstract Task toTask(TaskDTO taskDTO);

    /**
     * Извлекает ID автора задачи.
     *
     * @param author сущность пользователя-автора (может быть null)
     * @return ID автора или null, если автор не задан
     */
    @Named("mapAuthorId")
    public Long mapAuthorId(User author) {
        return author != null ? author.getId() : null;
    }

    /**
     * Извлекает ID исполнителя задачи.
     *
     * @param assignee сущность пользователя-исполнителя (может быть null)
     * @return ID исполнителя или null, если исполнитель не задан
     */
    @Named("mapAssigneeId")
    public Long mapAssigneeId(User assignee) {
        return assignee != null ? assignee.getId() : null;
    }
}