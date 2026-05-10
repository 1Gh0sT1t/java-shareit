package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    private User owner;

    @BeforeEach
    void setUp() {
        owner = userService.create(new User(null, "Owner", "owner@mail.ru"));
    }

    @Test
    void shouldCreateItem() {
        Item item = new Item(null, "Дрель", "Хорошая дрель", true, null, null);

        Item saved = itemService.create(item, owner.getId());

        assertNotNull(saved.getId());
        assertEquals("Дрель", saved.getName());
        assertEquals(owner.getId(), saved.getOwner().getId());
    }

    @Test
    void shouldGetItemsByOwnerId() {
        Item item1 = new Item(null, "Дрель", "Хорошая дрель", true, null, null);
        Item item2 = new Item(null, "Пила", "Хорошая пила", true, null, null);

        itemService.create(item1, owner.getId());
        itemService.create(item2, owner.getId());

        Collection<ItemResponseDto> items = itemService.getByOwnerId(owner.getId());

        assertEquals(2, items.size());
    }

    @Test
    void shouldReturnEmptyListForOwnerWithNoItems() {
        Collection<ItemResponseDto> items = itemService.getByOwnerId(owner.getId());

        assertTrue(items.isEmpty());
    }

    @Test
    void shouldSearchItems() {
        Item item = new Item(null, "Дрель", "Электрическая дрель", true, null, null);
        itemService.create(item, owner.getId());

        Collection<Item> found = itemService.search("дрель");

        assertEquals(1, found.size());
        assertEquals("Дрель", found.iterator().next().getName());
    }

    @Test
    void shouldReturnEmptyOnBlankSearch() {
        Collection<Item> result = itemService.search("  ");
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldUpdateItem() {
        Item item = itemService.create(
                new Item(null, "Дрель", "Хорошая дрель", true, null, null),
                owner.getId()
        );

        Item update = new Item(item.getId(), "Дрель Pro", null, null, null, null);
        Item updated = itemService.update(update, owner.getId());

        assertEquals("Дрель Pro", updated.getName());
        assertEquals("Хорошая дрель", updated.getDescription());
    }
}
