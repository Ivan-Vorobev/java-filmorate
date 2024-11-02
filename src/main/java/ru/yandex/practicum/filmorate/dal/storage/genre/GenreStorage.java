package ru.yandex.practicum.filmorate.dal.storage.genre;

import ru.yandex.practicum.filmorate.dal.model.FilmGenre;
import ru.yandex.practicum.filmorate.dal.model.Genre;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface GenreStorage {
    Collection<Genre> findAll();

    Optional<Genre> findById(Long genreId);

    Set<Long> findFilmGenres(Long filmId);

    Map<Long, Set<Long>> findAllFilmGenres();

    void delete(Long filmId);

    FilmGenre add(Long filmId, Long genreId);
}
