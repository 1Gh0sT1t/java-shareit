package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

/**
 * Маппер для вещи.
 */
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        if (item == null) {
            return null;
        }

        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static ItemResponseDto toItemResponseDto(Item item,
                                                    BookingShortDto lastBooking,
                                                    BookingShortDto nextBooking,
                                                    List<CommentDto> comments) {
        if (item == null) {
            return null;
        }

        return new ItemResponseDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null,
                lastBooking,
                nextBooking,
                comments
        );
    }

    public static BookingShortDto toBookingShortDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        return new BookingShortDto(
                booking.getId(),
                booking.getBooker().getId()
        );
    }

    public static Item toItem(ItemDto itemDto) {
        if (itemDto == null) {
            return null;
        }

        ItemRequest request = null;

        if (itemDto.getRequestId() != null) {
            request = new ItemRequest();
            request.setId(itemDto.getRequestId());
        }

        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                null,
                request
        );
    }
}