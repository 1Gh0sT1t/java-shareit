package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO вещи.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    // Идентификатор вещи
    private Long id;

    // Название вещи
    @Size(min = 1, message = "Название вещи не должно быть пустым")
    private String name;

    // Описание вещи
    @Size(min = 1, message = "Описание вещи не должно быть пустым")
    private String description;

    // Доступна ли вещь для аренды
    private Boolean available;

    // Идентификатор запроса (если есть)
    private Long requestId;
}