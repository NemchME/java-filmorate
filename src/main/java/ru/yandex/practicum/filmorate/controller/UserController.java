package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 1;

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("POST /users - Создание нового пользователя. Данные: {}", user);

        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Имя пользователя пустое, используем логин: {}", user.getLogin());
            user.setName(user.getLogin());
        }

        user.setId(nextId++);
        users.put(user.getId(), user);
        log.info("Пользователь успешно создан. ID: {}, Логин: {}", user.getId(), user.getLogin());
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("PUT /users - Обновление пользователя с ID: {}", user.getId());

        if (!users.containsKey(user.getId())) {
            log.warn("Пользователь с ID {} не найден", user.getId());
            throw new ResourceNotFoundException("Пользователь не найден");
        }

        users.put(user.getId(), user);
        log.info("Пользователь успешно обновлён. ID: {}, Новые данные: {}", user.getId(), user);
        return user;
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("GET /users - Запрос списка всех пользователей");
        List<User> allUsers = List.copyOf(users.values());
        log.debug("Найдено {} пользователей", allUsers.size());
        return allUsers;
    }
}