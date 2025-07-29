package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private UserController userController;
    private User validUser;

    @BeforeEach
    void setUp() {
        userController = new UserController();
        validUser = new User();
        validUser.setEmail("user@example.com");
        validUser.setLogin("validLogin");
        validUser.setBirthday(LocalDate.of(2000, 1, 1));
    }

    @Test
    void createUser_ValidUser_ShouldCreateUser() {
        User createdUser = userController.createUser(validUser);

        assertNotNull(createdUser.getId());
        assertEquals(validUser.getLogin(), createdUser.getLogin());
        assertEquals(1, userController.getAllUsers().size());
    }

    @Test
    void createUser_EmptyLogin_ShouldThrowValidationException() {
        validUser.setLogin("");

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.createUser(validUser));
        assertEquals("Логин не может быть пустым", exception.getMessage());
    }

    @Test
    void createUser_NullLogin_ShouldThrowValidationException() {
        validUser.setLogin(null);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.createUser(validUser));
        assertEquals("Логин не может быть пустым", exception.getMessage());
    }

    @Test
    void createUser_LoginWithSpaces_ShouldThrowValidationException() {
        validUser.setLogin("login with spaces");

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.createUser(validUser));
        assertEquals("Логин не должен содержать пробелы", exception.getMessage());
    }

    @Test
    void createUser_EmptyName_ShouldUseLoginAsName() {
        validUser.setName("");

        User createdUser = userController.createUser(validUser);
        assertEquals(validUser.getLogin(), createdUser.getName());
    }

    @Test
    void createUser_NullName_ShouldUseLoginAsName() {
        validUser.setName(null);

        User createdUser = userController.createUser(validUser);
        assertEquals(validUser.getLogin(), createdUser.getName());
    }

    @Test
    void createUser_InvalidEmail_ShouldThrowValidationException() {
        validUser.setEmail("invalid-email");

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.createUser(validUser));
        assertEquals("Электронная почта должна содержать @", exception.getMessage());
    }

    @Test
    void createUser_NullEmail_ShouldThrowValidationException() {
        validUser.setEmail(null);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.createUser(validUser));
        assertEquals("Электронная почта должна содержать @", exception.getMessage());
    }

    @Test
    void createUser_FutureBirthday_ShouldThrowValidationException() {
        validUser.setBirthday(LocalDate.now().plusDays(1));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.createUser(validUser));
        assertEquals("Дата рождения не может быть в будущем", exception.getMessage());
    }

    @Test
    void updateUser_NonExistentId_ShouldThrowValidationException() {
        validUser.setId(999);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.updateUser(validUser));
        assertEquals("Пользователь не найден", exception.getMessage());
    }
}