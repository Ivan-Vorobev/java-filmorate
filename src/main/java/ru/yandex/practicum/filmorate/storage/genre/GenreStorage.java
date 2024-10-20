package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.dal.dto.FilmGenreDto;
import ru.yandex.practicum.filmorate.dal.dto.GenreDto;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface GenreStorage {
    Collection<GenreDto> findAll();

    Optional<GenreDto> findById(Long genreId);

    Set<Long> findFilmGenres(Long filmId);

    Map<Long, Set<Long>> findAllFilmGenres();

    void delete(Long filmId);

    FilmGenreDto add(Long filmId, Long genreId);
}
