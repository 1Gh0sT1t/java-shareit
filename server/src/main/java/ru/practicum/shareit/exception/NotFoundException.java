package ru.practicum.shareit.exception;

/**
 * Ошибка, если объект не найден.
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}