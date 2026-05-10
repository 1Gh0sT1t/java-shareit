package ru.practicum.shareit.booking;

/**
 * Состояния для фильтрации бронирований.
 */
public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED
}