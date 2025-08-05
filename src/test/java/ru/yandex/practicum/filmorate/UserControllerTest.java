package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.impl.UserServiceImpl;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private UserController userController;
    private User validUser;
    private Validator validator;

    @BeforeEach
    void setUp() {
        userController = new UserController(new UserServiceImpl(new InMemoryUserStorage()));
        validUser = new User();
        validUser.setEmail("test@example.com");
        validUser.setLogin("testLogin");
        validUser.setName("Test User");
        validUser.setBirthday(LocalDate.of(2000, 1, 1));

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void addUser_ValidUser_ShouldAddUser() {
        User addedUser = userController.createUser(validUser);

        assertNotNull(addedUser.getId());
        assertEquals(validUser.getEmail(), addedUser.getEmail());
        assertEquals(1, userController.getAllUsers().size());
    }

    @Test
    void addUser_EmptyEmail_ShouldFailValidation() {
        validUser.setEmail("");

        Set<ConstraintViolation<User>> violations = validator.validate(validUser);
        assertFalse(violations.isEmpty());
        assertEquals("Электронная почта не может быть пустой", violations.iterator().next().getMessage());
    }

    @Test
    void addUser_InvalidEmail_ShouldFailValidation() {
        validUser.setEmail("invalid-email");

        Set<ConstraintViolation<User>> violations = validator.validate(validUser);
        assertFalse(violations.isEmpty());
        assertEquals("Электронная почта должна быть корректного формата", violations.iterator().next().getMessage());
    }

    @Test
    void addUser_LoginWithSpaces_ShouldFailValidation() {
        validUser.setLogin("login with spaces");

        Set<ConstraintViolation<User>> violations = validator.validate(validUser);
        assertFalse(violations.isEmpty());
        assertEquals("Логин не должен содержать пробелы", violations.iterator().next().getMessage());
    }

    @Test
    void addUser_FutureBirthday_ShouldFailValidation() {
        validUser.setBirthday(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<User>> violations = validator.validate(validUser);
        assertFalse(violations.isEmpty());
        assertEquals("Дата рождения не может быть в будущем", violations.iterator().next().getMessage());
    }

    @Test
    void updateUser_NonExistentId_ShouldThrowResourceNotFoundException() {
        validUser.setId(999);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> userController.updateUser(validUser));
        assertEquals("Пользователь с ID 999 не найден", exception.getMessage());
    }

    @Test
    void getUser_NonExistentId_ShouldThrowResourceNotFoundException() {
        int nonExistentId = 999;
        assertThrows(ResourceNotFoundException.class,
                () -> userController.getUser(nonExistentId));
    }

    @Test
    void deleteUser_ShouldRemoveUser() {
        User addedUser = userController.createUser(validUser);
        int userId = addedUser.getId();

        userController.deleteUser(userId);

        assertThrows(ResourceNotFoundException.class,
                () -> userController.getUser(userId));
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        userController.createUser(validUser);

        User anotherUser = new User();
        anotherUser.setEmail("another@example.com");
        anotherUser.setLogin("anotherLogin");
        anotherUser.setName("Another User");
        anotherUser.setBirthday(LocalDate.of(2001, 1, 1));
        userController.createUser(anotherUser);

        List<User> users = userController.getAllUsers();
        assertEquals(2, users.size());
    }
}