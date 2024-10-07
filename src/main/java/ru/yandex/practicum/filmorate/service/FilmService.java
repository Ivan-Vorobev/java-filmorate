package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FilmService {
    private final FilmStorage filmStorage;

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        return filmStorage.add(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Film addLike(final Long filmId, final Long userId) {
        Film film = filmStorage.findById(filmId);
        filmStorage.addLike(film, userId);

        return film;
    }

    public Film deleteLike(final Long filmId, final Long userId) {
        Film film = filmStorage.findById(filmId);
        filmStorage.deleteLike(film, userId);
        return film;
    }

    public Collection<Film> findTopPopular(final Long topCount) {
        if (topCount < 1) {
            throw new ValidationException("Count grater than 0");
        }

        return filmStorage.getAllLikes().entrySet().stream()
                .sorted((v1, v2) -> Integer.compare(v1.getValue().size(), v2.getValue().size()) * -1)
                .limit(topCount)
                .map(v -> filmStorage.findById(v.getKey()))
                .collect(Collectors.toList());


    }
}
