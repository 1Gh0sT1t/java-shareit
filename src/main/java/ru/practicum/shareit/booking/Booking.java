package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * Класс бронирования.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    // Идентификатор бронирования
    private Long id;

    // Дата и время начала бронирования
    private LocalDateTime start;

    // Дата и время окончания бронирования
    private LocalDateTime end;

    // Вещь, которую бронируют
    private Item item;

    // Пользователь, который бронирует
    private User booker;

    // Статус бронирования
    private BookingStatus status;
}