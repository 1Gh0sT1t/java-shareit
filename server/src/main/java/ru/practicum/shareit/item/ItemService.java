package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

/**
 * Интерфейс сервиса вещей.
 */
public interface ItemService {

    Item create(Item item, Long userId);

    Item update(Item item, Long userId);

    ItemResponseDto getById(Long itemId, Long userId);

    Collection<ItemResponseDto> getByOwnerId(Long ownerId);

    Collection<Item> search(String text);

    CommentDto addComment(Long itemId, Long userId, CommentDto commentDto);
}