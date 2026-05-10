package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO запроса вещи.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    // Идентификатор запроса
    private Long id;

    // Описание нужной вещи
    private String description;

    // Дата создания запроса
    private LocalDateTime created;

    // Вещи, которые добавили в ответ на запрос
    private List<ItemRequestItemDto> items;
}