package ru.practicum.shareit.request;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestItemDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Маппер запросов вещей.
 */
public class ItemRequestMapper {

    private ItemRequestMapper() {
    }

    public static ItemRequest toItemRequest(ItemRequestCreateDto dto, User requestor) {
        if (dto == null) {
            return null;
        }

        return new ItemRequest(
                null,
                dto.getDescription(),
                requestor,
                LocalDateTime.now()
        );
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest request, List<ItemRequestItemDto> items) {
        if (request == null) {
            return null;
        }

        return new ItemRequestDto(
                request.getId(),
                request.getDescription(),
                request.getCreated(),
                items
        );
    }

    public static ItemRequestItemDto toItemRequestItemDto(Item item) {
        if (item == null) {
            return null;
        }

        return new ItemRequestItemDto(
                item.getId(),
                item.getName(),
                item.getOwner().getId()
        );
    }
}