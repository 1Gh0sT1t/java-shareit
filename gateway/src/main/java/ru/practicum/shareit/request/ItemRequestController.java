package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

@Controller
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID_HEADER) long userId,
                                         @Valid @RequestBody ItemRequestCreateDto dto) {
        log.info("Creating request by user {}: {}", userId, dto);
        return itemRequestClient.create(userId, dto);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnRequests(@RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Get own requests for user {}", userId);
        return itemRequestClient.getOwnRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Get all requests for user {}", userId);
        return itemRequestClient.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader(USER_ID_HEADER) long userId,
                                          @PathVariable long requestId) {
        log.info("Get request {} for user {}", requestId, userId);
        return itemRequestClient.getById(userId, requestId);
    }
}
