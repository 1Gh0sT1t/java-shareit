package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;

/**
 * Интерфейс сервиса запросов вещей.
 */
public interface ItemRequestService {

    ItemRequestDto create(Long userId, ItemRequestCreateDto requestDto);

    Collection<ItemRequestDto> getOwnRequests(Long userId);

    Collection<ItemRequestDto> getAllRequests(Long userId);

    ItemRequestDto getById(Long userId, Long requestId);
}