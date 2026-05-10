package ru.practicum.shareit.booking;

/**
 * Статусы бронирования.
 */
public enum BookingStatus {
    // Новое бронирование, ожидает подтверждения
    WAITING,

    // Бронирование подтверждено владельцем
    APPROVED,

    // Бронирование отклонено владельцем
    REJECTED,

    // Бронирование отменено пользователем
    CANCELED
}