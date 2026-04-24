package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

/**
 * Интерфейс сервиса вещей.
 */
public interface ItemService {

    // Создаёт новую вещь
    Item create(Item item, Long userId);

    // Обновляет вещь
    Item update(Item item, Long userId);

    // Возвращает вещь по id
    Item getById(Long itemId);

    // Возвращает все вещи владельца
    Collection<Item> getByOwnerId(Long ownerId);

    // Ищет вещи по тексту
    Collection<Item> search(String text);
}