package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.storage.dal.dto.FilmDto;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {
    Collection<FilmDto> findAll();

    Optional<FilmDto> findById(Long filmId);

    FilmDto add(FilmDto film);

    FilmDto update(FilmDto film);

    void deleteLike(FilmDto film, Long userId);

    void addLike(FilmDto film, Long userId);

    Set<Long> getFilmLikes(FilmDto film);

    Map<Long, Set<Long>> getAllLikes();
}
