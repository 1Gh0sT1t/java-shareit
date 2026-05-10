package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * Класс комментария к вещи.
 */
@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    // Идентификатор комментария
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Текст комментария
    @Column(name = "text", nullable = false)
    private String text;

    // Вещь, к которой оставлен комментарий
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    // Автор комментария
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    // Дата и время создания комментария
    @Column(name = "created", nullable = false)
    private LocalDateTime created;
}