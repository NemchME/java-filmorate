package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import jakarta.validation.Valid;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserStorage userStorage;

    public UserController(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("POST /users - Создание нового пользователя. Данные: {}", user);
        User createdUser = userStorage.createUser(user);
        log.info("Пользователь успешно создан. ID: {}, Логин: {}", createdUser.getId(), createdUser.getLogin());
        return createdUser;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("PUT /users - Обновление пользователя с ID: {}", user.getId());

        if (userStorage.getUser(user.getId()) == null) {
            log.warn("Пользователь с ID {} не найден", user.getId());
            throw new ResourceNotFoundException("Пользователь не найден");
        }

        User updatedUser = userStorage.updateUser(user);
        log.info("Пользователь успешно обновлён. ID: {}, Новые данные: {}", updatedUser.getId(), updatedUser);
        return updatedUser;
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("GET /users - Запрос списка всех пользователей");
        List<User> allUsers = userStorage.getAllUsers();
        log.debug("Найдено {} пользователей", allUsers.size());
        return allUsers;
    }
}