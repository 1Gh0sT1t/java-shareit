package ru.practicum.shareit.user;

import java.util.Collection;

/**
 * Интерфейс хранилища пользователей.
 */
public interface UserStorage {

    // Сохраняет нового пользователя
    User create(User user);

    // Обновляет пользователя
    User update(User user);

    // Возвращает пользователя по id
    User getById(Long userId);

    // Возвращает всех пользователей
    Collection<User> getAll();

    // Удаляет пользователя по id
    void delete(Long userId);
}