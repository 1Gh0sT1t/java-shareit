package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Короткое DTO вещи для ответа на запрос.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestItemDto {
    // Идентификатор вещи
    private Long id;

    // Название вещи
    private String name;

    // Идентификатор владельца вещи
    private Long ownerId;
}