package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreateDto;

import java.util.Collection;

/**
 * Интерфейс сервиса бронирований.
 */
public interface BookingService {

    Booking create(BookingCreateDto bookingCreateDto, Long userId);

    Booking approve(Long bookingId, Long userId, Boolean approved);

    Booking getById(Long bookingId, Long userId);

    Collection<Booking> getByBooker(Long userId, BookingState state);

    Collection<Booking> getByOwner(Long ownerId, BookingState state);
}