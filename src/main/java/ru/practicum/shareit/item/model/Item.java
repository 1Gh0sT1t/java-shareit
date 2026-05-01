package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

/**
 * Класс вещи.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    // Идентификатор вещи
    private Long id;

    // Название вещи
    private String name;

    // Описание вещи
    private String description;

    // Доступна ли вещь для аренды
    private Boolean available;

    // Владелец вещи
    private User owner;

    // Запрос, по которому создана вещь (может быть null)
    private ItemRequest request;
}