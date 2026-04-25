package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Хранилище пользователей в памяти.
 */
@Component
public class InMemoryUserStorage implements UserStorage {
    // Хранилище пользователей
    private final Map<Long, User> users = new HashMap<>();

    // Счётчик идентификаторов
    private Long nextId = 1L;

    @Override
    public User create(User user) {
        // Устанавливаем новый идентификатор
        user.setId(getNextId());

        // Сохраняем пользователя
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        // Обновляем пользователя по идентификатору
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getById(Long userId) {
        // Возвращаем пользователя по идентификатору
        return users.get(userId);
    }

    @Override
    public User getByEmail(String email) {
        // Ищем пользователя по email
        for (User user : users.values()) {
            if (user.getEmail() != null && user.getEmail().equals(email)) {
                return user;
            }
        }

        return null;
    }

    @Override
    public Collection<User> getAll() {
        // Возвращаем всех пользователей
        return users.values();
    }

    @Override
    public void delete(Long userId) {
        // Удаляем пользователя по идентификатору
        users.remove(userId);
    }

    // Возвращает следующий идентификатор
    private Long getNextId() {
        return nextId++;
    }
}