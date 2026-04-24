package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Хранилище вещей в памяти.
 */
@Component
public class InMemoryItemStorage implements ItemStorage {
    // Хранилище вещей
    private final Map<Long, Item> items = new HashMap<>();

    // Счётчик идентификаторов
    private Long nextId = 1L;

    @Override
    public Item create(Item item) {
        // Устанавливаем новый идентификатор
        item.setId(getNextId());

        // Сохраняем вещь
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        // Обновляем вещь по идентификатору
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item getById(Long itemId) {
        // Возвращаем вещь по идентификатору
        return items.get(itemId);
    }

    @Override
    public Collection<Item> getByOwnerId(Long ownerId) {
        // Возвращаем все вещи владельца
        return items.values()
                .stream()
                .filter(item -> item.getOwner() != null && item.getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Item> search(String text) {
        // Переводим текст поиска в нижний регистр
        String lowerText = text.toLowerCase();

        // Ищем доступные вещи по названию и описанию
        return items.values()
                .stream()
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()))
                .filter(item -> containsText(item.getName(), lowerText)
                        || containsText(item.getDescription(), lowerText))
                .collect(Collectors.toList());
    }

    // Проверяет, содержит ли строка текст поиска
    private boolean containsText(String source, String text) {
        return source != null && source.toLowerCase().contains(text);
    }

    // Возвращает следующий идентификатор
    private Long getNextId() {
        return nextId++;
    }
}