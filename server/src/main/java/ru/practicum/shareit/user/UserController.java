package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

/**
 * Контроллер для работы с пользователями.
 */
@RestController
@RequestMapping(path = "/users")
public class UserController {
    // Сервис пользователей
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Создаёт нового пользователя
    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userService.create(user));
    }

    // Обновляет пользователя
    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId, @Valid @RequestBody UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        user.setId(userId);
        return UserMapper.toUserDto(userService.update(user));
    }

    // Возвращает пользователя по id
    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable Long userId) {
        return UserMapper.toUserDto(userService.getById(userId));
    }

    // Возвращает всех пользователей
    @GetMapping
    public Collection<UserDto> getAll() {
        return userService.getAll()
                .stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    // Удаляет пользователя по id
    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }
}