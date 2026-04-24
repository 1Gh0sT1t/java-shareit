package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * Класс запроса вещи.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {
    // Идентификатор запроса
    private Long id;

    // Описание запрашиваемой вещи
    private String description;

    // Пользователь, создавший запрос
    private User requestor;

    // Дата и время создания запроса
    private LocalDateTime created;
}