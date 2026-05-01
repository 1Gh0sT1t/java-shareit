package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

/**
 * Маппер для вещи.
 */
public class ItemMapper {

    // Преобразование Item -> ItemDto
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

    // Преобразование ItemDto -> Item
    public static Item toItem(ItemDto itemDto) {
        if (itemDto == null) {
            return null;
        }

        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                null, // owner задаётся в сервисе
                null  // request можно будет обработать позже
        );
    }
}