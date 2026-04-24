package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Тесты для хранилища вещей в памяти.
 */
class InMemoryItemStorageTest {
    // Хранилище вещей
    private InMemoryItemStorage itemStorage;

    // Владелец
    private User owner;

    @BeforeEach
    void setUp() {
        // Создаём новое хранилище перед каждым тестом
        itemStorage = new InMemoryItemStorage();

        // Создаём владельца
        owner = new User(1L, "Marat", "marat@mail.ru");
    }

    @Test
    void shouldCreateItem() {
        // Создаём вещь
        Item item = new Item(null, "Дрель", "Хорошая дрель", true, owner, null);

        // Сохраняем вещь
        Item savedItem = itemStorage.create(item);

        // Проверяем, что id назначен
        assertEquals(1L, savedItem.getId());

        // Проверяем, что вещь сохранилась
        assertEquals(savedItem, itemStorage.getById(savedItem.getId()));
    }

    @Test
    void shouldReturnItemsByOwnerId() {
        // Сохраняем вещи одного владельца
        itemStorage.create(new Item(null, "Дрель", "Хорошая дрель", true, owner, null));
        itemStorage.create(new Item(null, "Молоток", "Хороший молоток", true, owner, null));

        // Получаем вещи владельца
        Collection<Item> items = itemStorage.getByOwnerId(owner.getId());

        // Проверяем количество
        assertEquals(2, items.size());
    }

    @Test
    void shouldSearchOnlyAvailableItems() {
        // Сохраняем доступную вещь
        itemStorage.create(new Item(null, "Дрель", "Для ремонта", true, owner, null));

        // Сохраняем недоступную вещь
        itemStorage.create(new Item(null, "Шуруповерт", "Тоже для ремонта", false, owner, null));

        // Ищем по тексту
        Collection<Item> items = itemStorage.search("ремонт");

        // Проверяем, что нашлась только доступная вещь
        assertEquals(1, items.size());
    }

    @Test
    void shouldSearchByName() {
        // Сохраняем вещь
        itemStorage.create(new Item(null, "Дрель", "Для ремонта", true, owner, null));

        // Ищем по названию
        Collection<Item> items = itemStorage.search("дрель");

        // Проверяем результат
        assertEquals(1, items.size());
    }

    @Test
    void shouldSearchByDescription() {
        // Сохраняем вещь
        itemStorage.create(new Item(null, "Инструмент", "Хорошая дрель", true, owner, null));

        // Ищем по описанию
        Collection<Item> items = itemStorage.search("дрель");

        // Проверяем результат
        assertEquals(1, items.size());
    }
}