package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        Film updatedFilm = filmStorage.updateFilm(film);
        if (updatedFilm == null) {
            throw new ResourceNotFoundException("Фильм с ID " + film.getId() + " не найден");
        }
        return updatedFilm;
    }

    public Film getFilm(int id) {
        Film film = filmStorage.getFilm(id);
        if (film == null) {
            throw new ResourceNotFoundException("Фильм с ID " + id + " не найден");
        }
        return film;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public void deleteFilm(int id) {
        filmStorage.deleteFilm(id);
    }

    public void addLike(int filmId, int userId) {
        Film film = getFilm(filmId);
        userService.getUser(userId);

        if (film.getLikes().contains(userId)) {
            throw new ValidationException("Пользователь уже поставил лайк этому фильму");
        }
        film.getLikes().add(userId);
    }

    public void removeLike(int filmId, int userId) {
        Film film = getFilm(filmId);
        if (!film.getLikes().remove(userId)) {
            throw new ResourceNotFoundException("Лайк от пользователя " + userId + " не найден");
        }
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt(f -> -f.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    private Film getFilmOrThrow(int id) {
        Film film = filmStorage.getFilm(id);
        if (film == null) {
            throw new ResourceNotFoundException("Фильм с ID " + id + " не найден");
        }
        return film;
    }
}