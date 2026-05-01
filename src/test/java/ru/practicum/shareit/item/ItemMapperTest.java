package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Тесты для маппера вещей.
 */
class ItemMapperTest {

    @Test
    void shouldConvertItemToDto() {
        // Создаём владельца
        User owner = new User(1L, "Marat", "marat@mail.ru");

        // Создаём запрос
        ItemRequest request = new ItemRequest();
        request.setId(10L);

        // Создаём вещь
        Item item = new Item(2L, "Дрель", "Хорошая дрель", true, owner, request);

        // Преобразуем в dto
        ItemDto itemDto = ItemMapper.toItemDto(item);

        // Проверяем поля
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertEquals(request.getId(), itemDto.getRequestId());
    }

    @Test
    void shouldConvertDtoToItem() {
        // Создаём dto вещи
        ItemDto itemDto = new ItemDto(2L, "Дрель", "Хорошая дрель", true, 10L);

        // Преобразуем в модель
        Item item = ItemMapper.toItem(itemDto);

        // Проверяем поля
        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getAvailable(), item.getAvailable());
        assertNull(item.getOwner());
        assertNull(item.getRequest());
    }

    @Test
    void shouldReturnNullWhenItemIsNull() {
        // Проверяем преобразование null
        assertNull(ItemMapper.toItemDto(null));
    }

    @Test
    void shouldReturnNullWhenItemDtoIsNull() {
        // Проверяем преобразование null
        assertNull(ItemMapper.toItem(null));
    }
}