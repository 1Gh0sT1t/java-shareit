package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Тесты для маппера пользователей.
 */
class UserMapperTest {

    @Test
    void shouldConvertUserToDto() {
        // Создаём пользователя
        User user = new User(1L, "Marat", "marat@mail.ru");

        // Преобразуем в dto
        UserDto userDto = UserMapper.toUserDto(user);

        // Проверяем поля
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void shouldConvertDtoToUser() {
        // Создаём dto пользователя
        UserDto userDto = new UserDto(1L, "Marat", "marat@mail.ru");

        // Преобразуем в модель
        User user = UserMapper.toUser(userDto);

        // Проверяем поля
        assertEquals(userDto.getId(), user.getId());
        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());
    }

    @Test
    void shouldReturnNullWhenUserIsNull() {
        // Проверяем преобразование null
        assertNull(UserMapper.toUserDto(null));
    }

    @Test
    void shouldReturnNullWhenUserDtoIsNull() {
        // Проверяем преобразование null
        assertNull(UserMapper.toUser(null));
    }
}