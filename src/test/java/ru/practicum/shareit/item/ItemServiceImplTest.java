package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.InMemoryUserStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Тесты для сервиса вещей.
 */
class ItemServiceImplTest {
    // Сервис вещей
    private ItemServiceImpl itemService;

    // Хранилище вещей
    private ItemStorage itemStorage;

    // Хранилище пользователей
    private UserStorage userStorage;

    // Владелец
    private User owner;

    @BeforeEach
    void setUp() {
        // Создаём хранилища
        itemStorage = new InMemoryItemStorage();
        userStorage = new InMemoryUserStorage();

        // Создаём сервис
        itemService = new ItemServiceImpl(itemStorage, userStorage);

        // Сохраняем владельца
        owner = userStorage.create(new User(null, "Marat", "marat@mail.ru"));
    }

    @Test
    void shouldCreateItem() {
        // Создаём вещь
        Item item = new Item(null, "Дрель", "Хорошая дрель", true, null, null);

        // Сохраняем вещь
        Item savedItem = itemService.create(item, owner.getId());

        // Проверяем сохранение
        assertEquals(1L, savedItem.getId());
        assertEquals("Дрель", savedItem.getName());
        assertEquals(owner.getId(), savedItem.getOwner().getId());
    }

    @Test
    void shouldThrowExceptionWhenOwnerNotFound() {
        // Создаём вещь
        Item item = new Item(null, "Дрель", "Хорошая дрель", true, null, null);

        // Проверяем ошибку
        assertThrows(NotFoundException.class, () -> itemService.create(item, 999L));
    }

    @Test
    void shouldThrowExceptionWhenAvailableIsNull() {
        // Создаём вещь без статуса доступности
        Item item = new Item(null, "Дрель", "Хорошая дрель", null, null, null);

        // Проверяем ошибку
        assertThrows(ValidationException.class, () -> itemService.create(item, owner.getId()));
    }

    @Test
    void shouldUpdateOnlyDescription() {
        // Сохраняем вещь
        Item savedItem = itemService.create(
                new Item(null, "Дрель", "Старое описание", true, null, null),
                owner.getId()
        );

        // Создаём объект для частичного обновления
        Item itemForUpdate = new Item(savedItem.getId(), null, "Новое описание", null, null, null);

        // Обновляем вещь
        Item updatedItem = itemService.update(itemForUpdate, owner.getId());

        // Проверяем обновление
        assertEquals("Дрель", updatedItem.getName());
        assertEquals("Новое описание", updatedItem.getDescription());
        assertEquals(true, updatedItem.getAvailable());
    }

    @Test
    void shouldThrowExceptionWhenOtherUserUpdatesItem() {
        // Сохраняем второго пользователя
        User otherUser = userStorage.create(new User(null, "Other", "other@mail.ru"));

        // Сохраняем вещь
        Item savedItem = itemService.create(
                new Item(null, "Дрель", "Описание", true, null, null),
                owner.getId()
        );

        // Создаём объект для обновления
        Item itemForUpdate = new Item(savedItem.getId(), "Новое имя", null, null, null, null);

        // Проверяем ошибку
        assertThrows(NotFoundException.class, () -> itemService.update(itemForUpdate, otherUser.getId()));
    }

    @Test
    void shouldReturnEmptyListWhenSearchTextIsBlank() {
        // Ищем по пустой строке
        Collection<Item> items = itemService.search(" ");

        // Проверяем, что список пустой
        assertEquals(0, items.size());
    }

    @Test
    void shouldSearchAvailableItems() {
        // Сохраняем доступную вещь
        itemService.create(new Item(null, "Дрель", "Для ремонта", true, null, null), owner.getId());

        // Сохраняем недоступную вещь
        itemService.create(new Item(null, "Шуруповерт", "Для ремонта", false, null, null), owner.getId());

        // Ищем вещи
        Collection<Item> items = itemService.search("ремонт");

        // Проверяем, что нашлась только доступная вещь
        assertEquals(1, items.size());
    }
}