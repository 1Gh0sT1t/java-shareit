package ru.practicum.shareit.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Обработчик ошибок приложения.
 */
@RestControllerAdvice
public class ErrorHandler {

    // Обрабатывает ошибку, если объект не найден
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    // Обрабатывает ошибку конфликта данных
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictException(final ConflictException e) {
        return new ErrorResponse(e.getMessage());
    }

    // Обрабатывает ошибку валидации данных
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        return new ErrorResponse(e.getMessage());
    }

    // Обрабатывает ошибки валидации @Valid
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        return new ErrorResponse("Переданы некорректные данные");
    }

    // Обрабатывает ошибки ограничений валидации
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(final ConstraintViolationException e) {
        return new ErrorResponse("Переданы некорректные данные");
    }

    // Обрабатывает остальные ошибки некорректных данных
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(final IllegalArgumentException e) {
        return new ErrorResponse(e.getMessage());
    }

    // Обрабатывает остальные ошибки конфликта
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleIllegalStateException(final IllegalStateException e) {
        return new ErrorResponse(e.getMessage());
    }
}