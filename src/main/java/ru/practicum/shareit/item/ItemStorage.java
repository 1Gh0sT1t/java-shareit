package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

/**
 * Интерфейс хранилища вещей.
 */
public interface ItemStorage {

    // Сохраняет новую вещь
    Item create(Item item);

    // Обновляет вещь
    Item update(Item item);

    // Возвращает вещь по id
    Item getById(Long itemId);

    // Возвращает все вещи владельца
    Collection<Item> getByOwnerId(Long ownerId);

    // Ищет вещи по тексту
    Collection<Item> search(String text);
}