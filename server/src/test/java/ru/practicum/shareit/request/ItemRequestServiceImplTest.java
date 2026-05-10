package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User requestor;
    private User otherUser;
    private ItemRequest request;
    private Item item;

    @BeforeEach
    void setUp() {
        requestor = new User(1L, "Requestor", "requestor@mail.ru");
        otherUser = new User(2L, "Other", "other@mail.ru");

        request = new ItemRequest(
                10L,
                "Нужна дрель",
                requestor,
                LocalDateTime.now().minusDays(1)
        );

        item = new Item(100L, "Дрель", "Хорошая дрель", true, otherUser, request);
    }

    @Test
    void shouldCreateRequest() {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto("Нужна дрель");

        when(userRepository.findById(requestor.getId())).thenReturn(Optional.of(requestor));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(request);

        ItemRequestDto result = itemRequestService.create(requestor.getId(), createDto);

        assertNotNull(result);
        assertEquals(request.getId(), result.getId());
        assertEquals(request.getDescription(), result.getDescription());
        assertNotNull(result.getItems());

        verify(userRepository).findById(requestor.getId());
        verify(itemRequestRepository).save(any(ItemRequest.class));
    }

    @Test
    void shouldThrowNotFoundWhenUserNotFoundOnCreate() {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto("Нужна дрель");

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.create(99L, createDto));
    }

    @Test
    void shouldGetOwnRequests() {
        when(userRepository.findById(requestor.getId())).thenReturn(Optional.of(requestor));
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(requestor.getId()))
                .thenReturn(List.of(request));
        when(itemRepository.findByRequestIdIn(List.of(request.getId())))
                .thenReturn(List.of(item));

        Collection<ItemRequestDto> result = itemRequestService.getOwnRequests(requestor.getId());

        assertEquals(1, result.size());
        ItemRequestDto dto = result.iterator().next();
        assertEquals(request.getId(), dto.getId());
        assertEquals(1, dto.getItems().size());
        assertEquals(item.getId(), dto.getItems().get(0).getId());

        verify(userRepository).findById(requestor.getId());
        verify(itemRequestRepository).findByRequestorIdOrderByCreatedDesc(requestor.getId());
    }

    @Test
    void shouldReturnEmptyListWhenNoOwnRequests() {
        when(userRepository.findById(requestor.getId())).thenReturn(Optional.of(requestor));
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(requestor.getId()))
                .thenReturn(List.of());

        Collection<ItemRequestDto> result = itemRequestService.getOwnRequests(requestor.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldThrowNotFoundWhenUserNotFoundOnGetOwnRequests() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getOwnRequests(99L));
    }

    @Test
    void shouldGetAllRequests() {
        when(userRepository.findById(otherUser.getId())).thenReturn(Optional.of(otherUser));
        when(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(otherUser.getId()))
                .thenReturn(List.of(request));
        when(itemRepository.findByRequestIdIn(List.of(request.getId())))
                .thenReturn(List.of(item));

        Collection<ItemRequestDto> result = itemRequestService.getAllRequests(otherUser.getId());

        assertEquals(1, result.size());
        ItemRequestDto dto = result.iterator().next();
        assertEquals(request.getId(), dto.getId());
    }

    @Test
    void shouldThrowNotFoundWhenUserNotFoundOnGetAllRequests() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getAllRequests(99L));
    }

    @Test
    void shouldGetRequestById() {
        when(userRepository.findById(otherUser.getId())).thenReturn(Optional.of(otherUser));
        when(itemRequestRepository.findById(request.getId())).thenReturn(Optional.of(request));
        when(itemRepository.findByRequestId(request.getId())).thenReturn(List.of(item));

        ItemRequestDto result = itemRequestService.getById(otherUser.getId(), request.getId());

        assertNotNull(result);
        assertEquals(request.getId(), result.getId());
        assertEquals(request.getDescription(), result.getDescription());
        assertEquals(1, result.getItems().size());
        assertEquals(item.getId(), result.getItems().get(0).getId());
        assertEquals(otherUser.getId(), result.getItems().get(0).getOwnerId());

        verify(itemRequestRepository).findById(request.getId());
        verify(itemRepository).findByRequestId(request.getId());
    }

    @Test
    void shouldThrowNotFoundWhenUserNotFoundOnGetById() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getById(99L, request.getId()));
    }

    @Test
    void shouldThrowNotFoundWhenRequestNotFoundOnGetById() {
        when(userRepository.findById(otherUser.getId())).thenReturn(Optional.of(otherUser));
        when(itemRequestRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getById(otherUser.getId(), 99L));
    }

    @Test
    void shouldGetRequestByIdWithNoItems() {
        when(userRepository.findById(otherUser.getId())).thenReturn(Optional.of(otherUser));
        when(itemRequestRepository.findById(request.getId())).thenReturn(Optional.of(request));
        when(itemRepository.findByRequestId(request.getId())).thenReturn(List.of());

        ItemRequestDto result = itemRequestService.getById(otherUser.getId(), request.getId());

        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
    }
}
