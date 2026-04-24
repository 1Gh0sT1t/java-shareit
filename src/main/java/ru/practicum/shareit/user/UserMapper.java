package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

/**
 * Маппер для пользователя.
 */
public class UserMapper {

    // Преобразование User -> UserDto
    public static UserDto toUserDto(User user) {
        if (user == null) {
            return null;
        }

        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    // Преобразование UserDto -> User
    public static User toUser(UserDto userDto) {
        if (userDto == null) {
            return null;
        }

        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail()
        );
    }
}