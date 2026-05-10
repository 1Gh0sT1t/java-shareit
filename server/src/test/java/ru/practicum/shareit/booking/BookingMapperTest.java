package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Тесты маппера бронирований.
 */
class BookingMapperTest {

    @Test
    void shouldConvertBookingToDto() {
        User owner = new User(1L, "Owner", "owner@mail.ru");
        User booker = new User(2L, "Booker", "booker@mail.ru");

        Item item = new Item(
                10L,
                "Дрель",
                "Хорошая дрель",
                true,
                owner,
                null
        );

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusDays(2);

        Booking booking = new Booking(
                100L,
                start,
                end,
                item,
                booker,
                BookingStatus.WAITING
        );

        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(start, bookingDto.getStart());
        assertEquals(end, bookingDto.getEnd());
        assertEquals(BookingStatus.WAITING, bookingDto.getStatus());

        assertEquals(item.getId(), bookingDto.getItem().getId());
        assertEquals(item.getName(), bookingDto.getItem().getName());

        assertEquals(booker.getId(), bookingDto.getBooker().getId());
        assertEquals(booker.getName(), bookingDto.getBooker().getName());
        assertEquals(booker.getEmail(), bookingDto.getBooker().getEmail());
    }

    @Test
    void shouldReturnNullWhenBookingIsNull() {
        assertNull(BookingMapper.toBookingDto(null));
    }
}