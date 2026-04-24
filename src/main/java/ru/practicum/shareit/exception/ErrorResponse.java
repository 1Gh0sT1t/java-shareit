package ru.practicum.shareit.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Ответ с текстом ошибки.
 */
@Data
@AllArgsConstructor
public class ErrorResponse {
    // Текст ошибки
    private String error;
}