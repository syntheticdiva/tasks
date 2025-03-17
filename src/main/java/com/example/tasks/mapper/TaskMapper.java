package com.example.tasks.mapper;

import com.example.tasks.dto.TaskDTO;
import com.example.tasks.entity.Task;
import com.example.tasks.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {CommentMapper.class})
public abstract class TaskMapper {

    @Mapping(source = "author", target = "authorId", qualifiedByName = "mapAuthorId")
    @Mapping(source = "assignee", target = "assigneeId", qualifiedByName = "mapAssigneeId")
    public abstract TaskDTO toTaskDTO(Task task);

    @Mapping(target = "author", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    public abstract Task toTask(TaskDTO taskDTO);

    @Named("mapAuthorId")
    public Long mapAuthorId(User author) {
        return author != null ? author.getId() : null;
    }

    @Named("mapAssigneeId")
    public Long mapAssigneeId(User assignee) {
        return assignee != null ? assignee.getId() : null;
    }

}
