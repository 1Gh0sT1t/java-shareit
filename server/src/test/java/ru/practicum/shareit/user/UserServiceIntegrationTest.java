package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void shouldCreateAndGetUser() {
        User user = new User(null, "Alice", "alice@mail.ru");

        User saved = userService.create(user);

        assertNotNull(saved.getId());
        assertEquals("Alice", saved.getName());
        assertEquals("alice@mail.ru", saved.getEmail());

        User found = userService.getById(saved.getId());
        assertEquals(saved.getId(), found.getId());
    }

    @Test
    void shouldGetAllUsers() {
        userService.create(new User(null, "Alice", "alice@mail.ru"));
        userService.create(new User(null, "Bob", "bob@mail.ru"));

        Collection<User> users = userService.getAll();

        assertEquals(2, users.size());
    }

    @Test
    void shouldUpdateUser() {
        User user = userService.create(new User(null, "Alice", "alice@mail.ru"));

        User update = new User(user.getId(), "Alice Updated", null);
        User updated = userService.update(update);

        assertEquals("Alice Updated", updated.getName());
        assertEquals("alice@mail.ru", updated.getEmail());
    }

    @Test
    void shouldDeleteUser() {
        User user = userService.create(new User(null, "Alice", "alice@mail.ru"));
        Long id = user.getId();

        userService.delete(id);

        assertThrows(NotFoundException.class, () -> userService.getById(id));
    }

    @Test
    void shouldThrowConflictOnDuplicateEmail() {
        userService.create(new User(null, "Alice", "alice@mail.ru"));

        assertThrows(ConflictException.class,
                () -> userService.create(new User(null, "Another", "alice@mail.ru")));
    }
}
