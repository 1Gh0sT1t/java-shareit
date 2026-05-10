package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Короткая информация о бронировании.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingShortDto {
    private Long id;
    private Long bookerId;
}