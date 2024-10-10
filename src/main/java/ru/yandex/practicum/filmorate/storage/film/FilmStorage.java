package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface FilmStorage {
    Collection<Film> findAll();

    Film findById(Long filmId);

    Film add(Film film);

    Film update(Film film);

    void deleteLike(Film film, Long userId);

    void addLike(Film film, Long userId);

    Set<Long> getFilmLikes(Film film);

    Map<Long, Set<Long>> getAllLikes();
}
