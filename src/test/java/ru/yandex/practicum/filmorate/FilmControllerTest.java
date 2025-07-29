package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private FilmController filmController;
    private Film validFilm;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
        validFilm = new Film();
        validFilm.setName("Valid Film");
        validFilm.setDescription("Valid description");
        validFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        validFilm.setDuration(120);
    }

    @Test
    void addFilm_ValidFilm_ShouldAddFilm() {
        Film addedFilm = filmController.addFilm(validFilm);

        assertNotNull(addedFilm.getId());
        assertEquals(validFilm.getName(), addedFilm.getName());
        assertEquals(1, filmController.getAllFilms().size());
    }

    @Test
    void addFilm_EmptyName_ShouldThrowValidationException() {
        validFilm.setName("");

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.addFilm(validFilm));
        assertEquals("Название фильма не может быть пустым", exception.getMessage());
    }

    @Test
    void addFilm_NullName_ShouldThrowValidationException() {
        validFilm.setName(null);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.addFilm(validFilm));
        assertEquals("Название фильма не может быть пустым", exception.getMessage());
    }

    @Test
    void addFilm_TooLongDescription_ShouldThrowValidationException() {
        String longDescription = "a".repeat(201);
        validFilm.setDescription(longDescription);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.addFilm(validFilm));
        assertEquals("Описание не должно превышать 200 символов", exception.getMessage());
    }

    @Test
    void addFilm_ReleaseDateBeforeMinDate_ShouldThrowValidationException() {
        validFilm.setReleaseDate(LocalDate.of(1895, 12, 27));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.addFilm(validFilm));
        assertEquals("Дата релиза должна быть после 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    void addFilm_NullReleaseDate_ShouldThrowValidationException() {
        validFilm.setReleaseDate(null);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.addFilm(validFilm));
        assertEquals("Дата релиза должна быть после 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    void addFilm_ZeroDuration_ShouldThrowValidationException() {
        validFilm.setDuration(0);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.addFilm(validFilm));
        assertEquals("Продолжительность фильма должна быть положительной", exception.getMessage());
    }

    @Test
    void addFilm_NegativeDuration_ShouldThrowValidationException() {
        validFilm.setDuration(-10);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.addFilm(validFilm));
        assertEquals("Продолжительность фильма должна быть положительной", exception.getMessage());
    }

    @Test
    void updateFilm_NonExistentId_ShouldThrowResourceNotFoundException() {
        validFilm.setId(999);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> filmController.updateFilm(validFilm));
        assertEquals("Фильм не найден", exception.getMessage());
    }
}