package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Сервис для работы с запросами вещей.
 */
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto create(Long userId, ItemRequestCreateDto requestDto) {
        User requestor = getUserOrThrow(userId);
        ItemRequest request = ItemRequestMapper.toItemRequest(requestDto, requestor);

        return ItemRequestMapper.toItemRequestDto(
                itemRequestRepository.save(request),
                Collections.emptyList()
        );
    }

    @Override
    public Collection<ItemRequestDto> getOwnRequests(Long userId) {
        getUserOrThrow(userId);

        List<ItemRequest> requests = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(userId);
        return toDtosWithItems(requests);
    }

    @Override
    public Collection<ItemRequestDto> getAllRequests(Long userId) {
        getUserOrThrow(userId);

        List<ItemRequest> requests = itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(userId);
        return toDtosWithItems(requests);
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        getUserOrThrow(userId);

        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос вещи с таким id не найден"));

        List<ItemRequestItemDto> items = itemRepository.findByRequestId(requestId)
                .stream()
                .map(ItemRequestMapper::toItemRequestItemDto)
                .toList();

        return ItemRequestMapper.toItemRequestDto(request, items);
    }

    private List<ItemRequestDto> toDtosWithItems(List<ItemRequest> requests) {
        if (requests.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .toList();

        Map<Long, List<ItemRequestItemDto>> itemsByRequestId = itemRepository.findByRequestIdIn(requestIds)
                .stream()
                .collect(Collectors.groupingBy(
                        item -> item.getRequest().getId(),
                        Collectors.mapping(ItemRequestMapper::toItemRequestItemDto, Collectors.toList())
                ));

        return requests.stream()
                .map(request -> ItemRequestMapper.toItemRequestDto(
                        request,
                        itemsByRequestId.getOrDefault(request.getId(), Collections.emptyList())
                ))
                .toList();
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким id не найден"));
    }
}