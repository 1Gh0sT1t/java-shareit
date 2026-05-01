package ru.practicum.shareit.exception;

/**
 * Ошибка конфликта данных.
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}