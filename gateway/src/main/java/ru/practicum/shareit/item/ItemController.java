package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import static ru.practicum.shareit.util.ApiConstants.USER_ID_HEADER;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID_HEADER) long userId,
                                         @Valid @RequestBody ItemCreateDto dto) {
        log.info("Creating item by user {}: {}", userId, dto);
        return itemClient.create(userId, dto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@PathVariable long itemId,
                                         @RequestHeader(USER_ID_HEADER) long userId,
                                         @Valid @RequestBody ItemUpdateDto dto) {
        log.info("Updating item {} by user {}: {}", itemId, userId, dto);
        return itemClient.update(userId, itemId, dto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@PathVariable long itemId,
                                          @RequestHeader(value = USER_ID_HEADER, required = false) Long userId) {
        log.info("Get item {}", itemId);
        return itemClient.getById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getByOwnerId(@RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Get items for owner {}", userId);
        return itemClient.getByOwnerId(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text) {
        log.info("Search items: {}", text);
        return itemClient.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable long itemId,
                                             @RequestHeader(USER_ID_HEADER) long userId,
                                             @Valid @RequestBody CommentCreateDto dto) {
        log.info("Adding comment to item {} by user {}", itemId, userId);
        return itemClient.addComment(userId, itemId, dto);
    }
}