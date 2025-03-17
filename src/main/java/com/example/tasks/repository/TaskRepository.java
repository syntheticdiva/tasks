package com.example.tasks.repository;

import com.example.tasks.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    List<Task> findByAuthorId(Long authorId);
    List<Task> findByAssigneeId(Long assigneeId);

    Page<Task> findAll(Specification<Task> spec, Pageable pageable);

}