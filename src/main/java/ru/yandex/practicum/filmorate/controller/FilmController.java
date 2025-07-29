package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final Map<Integer, Film> films = new HashMap<>();
    private int nextId = 1;

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        log.info("POST /films - Добавление нового фильма: {}", film);
        try {
            validateFilm(film);
            film.setId(nextId++);
            films.put(film.getId(), film);
            log.info("Фильм успешно добавлен. ID: {}, Название: {}", film.getId(), film.getName());
            return film;
        } catch (ValidationException e) {
            log.error("Ошибка при добавлении фильма: {}", e.getMessage());
            throw e;
        }
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        log.info("PUT /films - Обновление фильма с ID: {}", film.getId());
        try {
            if (!films.containsKey(film.getId())) {
                log.warn("Фильм с ID {} не найден", film.getId());
                throw new ResourceNotFoundException("Фильм не найден");
            }
            validateFilm(film);
            films.put(film.getId(), film);
            log.info("Фильм успешно обновлён. ID: {}, Новые данные: {}", film.getId(), film);
            return film;
        } catch (ValidationException e) {
            log.error("Ошибка при обновлении фильма ID {}: {}", film.getId(), e.getMessage());
            throw e;
        }
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("GET /films - Запрос всех фильмов");
        List<Film> allFilms = List.copyOf(films.values());
        log.debug("Найдено {} фильмов", allFilms.size());
        return allFilms;
    }

    private void validateFilm(Film film) {
        log.debug("Валидация фильма: {}", film);

        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Некорректное название фильма: {}", film.getName());
            throw new ValidationException("Название фильма не может быть пустым");
        }

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.warn("Слишком длинное описание ({} символов)", film.getDescription().length());
            throw new ValidationException("Описание не должно превышать 200 символов");
        }

        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.warn("Некорректная дата релиза: {}", film.getReleaseDate());
            throw new ValidationException("Дата релиза должна быть после 28 декабря 1895 года");
        }

        if (film.getDuration() <= 0) {
            log.warn("Некорректная продолжительность: {}", film.getDuration());
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }

        log.debug("Валидация пройдена успешно");
    }
}