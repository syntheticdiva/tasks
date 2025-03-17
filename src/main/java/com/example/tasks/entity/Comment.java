package com.example.tasks.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

/**
 * Представляет комментарий к задаче в системе.
 * <p>
 * Комментарий содержит текст, связь с задачей и автором.
 * Является JPA-сущностью для хранения в базе данных.
 * </p>
 *
 * @author AlinaSheveleva
 * @version 1.0
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String text;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return Objects.equals(id, comment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
