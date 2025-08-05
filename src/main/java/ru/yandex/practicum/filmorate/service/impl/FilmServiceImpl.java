package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserServiceImpl userServiceImpl;

    @Autowired
    public FilmServiceImpl(FilmStorage filmStorage, UserServiceImpl userServiceImpl) {
        this.filmStorage = filmStorage;
        this.userServiceImpl = userServiceImpl;
    }

    @Override
    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        Film updatedFilm = filmStorage.updateFilm(film);
        if (updatedFilm == null) {
            throw new ResourceNotFoundException("Фильм с ID " + film.getId() + " не найден");
        }
        return updatedFilm;
    }

    @Override
    public Film getFilm(int id) {
        Film film = filmStorage.getFilm(id);
        if (film == null) {
            throw new ResourceNotFoundException("Фильм с ID " + id + " не найден");
        }
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    @Override
    public void deleteFilm(int id) {
        filmStorage.deleteFilm(id);
    }

    @Override
    public void addLike(int filmId, int userId) {
        Film film = getFilm(filmId);
        userServiceImpl.getUser(userId);

        if (film.getLikes().contains(userId)) {
            throw new ValidationException("Пользователь уже поставил лайк этому фильму");
        }
        film.getLikes().add(userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        Film film = getFilm(filmId);
        if (!film.getLikes().remove(userId)) {
            throw new ResourceNotFoundException("Лайк от пользователя " + userId + " не найден");
        }
    }

    @Override
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