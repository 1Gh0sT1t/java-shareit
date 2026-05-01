package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Тесты репозитория вещей.
 */
@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindItemsByOwnerId() {
        User owner = userRepository.save(new User(null, "Owner", "owner@mail.ru"));

        Item firstItem = new Item(null, "Дрель", "Хорошая дрель", true, owner, null);
        Item secondItem = new Item(null, "Молоток", "Хороший молоток", true, owner, null);

        itemRepository.save(firstItem);
        itemRepository.save(secondItem);

        List<Item> result = itemRepository.findByOwnerId(owner.getId());

        assertEquals(2, result.size());
    }

    @Test
    void shouldSearchAvailableItemsByName() {
        User owner = userRepository.save(new User(null, "Owner", "owner@mail.ru"));

        itemRepository.save(new Item(null, "Дрель", "Для ремонта", true, owner, null));
        itemRepository.save(new Item(null, "Молоток", "Для стройки", true, owner, null));

        List<Item> result = itemRepository.search("дрель");

        assertEquals(1, result.size());
        assertEquals("Дрель", result.getFirst().getName());
    }

    @Test
    void shouldSearchAvailableItemsByDescription() {
        User owner = userRepository.save(new User(null, "Owner", "owner@mail.ru"));

        itemRepository.save(new Item(null, "Инструмент", "Хорошая дрель", true, owner, null));
        itemRepository.save(new Item(null, "Молоток", "Для стройки", true, owner, null));

        List<Item> result = itemRepository.search("дрель");

        assertEquals(1, result.size());
        assertEquals("Инструмент", result.getFirst().getName());
    }

    @Test
    void shouldNotReturnUnavailableItemsFromSearch() {
        User owner = userRepository.save(new User(null, "Owner", "owner@mail.ru"));

        itemRepository.save(new Item(null, "Дрель", "Для ремонта", false, owner, null));

        List<Item> result = itemRepository.search("дрель");

        assertEquals(0, result.size());
    }
}