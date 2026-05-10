package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    private User requestor;
    private User otherUser;

    @BeforeEach
    void setUp() {
        requestor = userService.create(new User(null, "Requestor", "requestor@mail.ru"));
        otherUser = userService.create(new User(null, "Other", "other@mail.ru"));
    }

    @Test
    void shouldCreateRequest() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto("Нужна дрель");

        ItemRequestDto result = itemRequestService.create(requestor.getId(), dto);

        assertNotNull(result.getId());
        assertEquals("Нужна дрель", result.getDescription());
        assertNotNull(result.getCreated());
        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void shouldGetOwnRequests() {
        itemRequestService.create(requestor.getId(), new ItemRequestCreateDto("Нужна дрель"));
        itemRequestService.create(requestor.getId(), new ItemRequestCreateDto("Нужна пила"));

        Collection<ItemRequestDto> results = itemRequestService.getOwnRequests(requestor.getId());

        assertEquals(2, results.size());
    }

    @Test
    void shouldGetOwnRequestsWithItems() {
        ItemRequestDto request = itemRequestService.create(
                requestor.getId(), new ItemRequestCreateDto("Нужна дрель")
        );

        Item item = new Item(null, "Дрель", "Хорошая дрель", true, null, null);
        item.setRequest(new ItemRequest());
        item.getRequest().setId(request.getId());
        itemService.create(item, otherUser.getId());

        Collection<ItemRequestDto> results = itemRequestService.getOwnRequests(requestor.getId());

        assertEquals(1, results.size());
        assertEquals(1, results.iterator().next().getItems().size());
    }

    @Test
    void shouldGetAllRequests() {
        itemRequestService.create(requestor.getId(), new ItemRequestCreateDto("Нужна дрель"));

        Collection<ItemRequestDto> results = itemRequestService.getAllRequests(otherUser.getId());

        assertEquals(1, results.size());
        assertEquals("Нужна дрель", results.iterator().next().getDescription());
    }

    @Test
    void shouldNotSeeOwnRequestsInGetAll() {
        itemRequestService.create(requestor.getId(), new ItemRequestCreateDto("Нужна дрель"));

        Collection<ItemRequestDto> results = itemRequestService.getAllRequests(requestor.getId());

        assertTrue(results.isEmpty());
    }

    @Test
    void shouldGetRequestById() {
        ItemRequestDto created = itemRequestService.create(
                requestor.getId(), new ItemRequestCreateDto("Нужна дрель")
        );

        ItemRequestDto found = itemRequestService.getById(otherUser.getId(), created.getId());

        assertEquals(created.getId(), found.getId());
        assertEquals("Нужна дрель", found.getDescription());
    }
}
