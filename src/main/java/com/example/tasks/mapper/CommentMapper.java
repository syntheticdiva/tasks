package com.example.tasks.mapper;

import com.example.tasks.dto.CommentDTO;
import com.example.tasks.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Маппер для преобразования между сущностью {@link Comment} и DTO {@link CommentDTO}.
 * <p>
 * Использует MapStruct для автоматической генерации реализации. Интегрирован со Spring через
 * {@code componentModel = "spring"}, что позволяет использовать его как Spring-бин.
 * </p>
 *
 * @see Mapper
 */
@Mapper(componentModel = "spring")
public interface CommentMapper {

    /**
     * Преобразует сущность {@link Comment} в {@link CommentDTO}.
     * <p>
     * Особенности преобразования:
     * <ul>
     *   <li>ID задачи (comment.task.id) → CommentDTO.taskId</li>
     *   <li>ID автора (comment.author.id) → CommentDTO.authorId</li>
     *   <li>Остальные поля маппятся автоматически по совпадению имен</li>
     * </ul>
     *
     * @param comment исходная сущность комментария (не {@code null})
     * @return DTO комментария с заполненными taskId и authorId
     */
    @Mapping(source = "task.id", target = "taskId")
    @Mapping(source = "author.id", target = "authorId")
    CommentDTO toCommentDTO(Comment comment);

    /**
     * Преобразует {@link CommentDTO} в сущность {@link Comment}.
     * <p>
     * Особенности преобразования:
     * <ul>
     *   <li>Поля task и author игнорируются (требуют отдельной обработки)</li>
     *   <li>Остальные поля маппятся автоматически по совпадению имен</li>
     * </ul>
     *
     * @param commentDTO DTO комментария (не {@code null})
     * @return сущность комментария без связей с задачей и автором
     */
    @Mapping(target = "task", ignore = true)
    @Mapping(target = "author", ignore = true)
    Comment toComment(CommentDTO commentDTO);
}