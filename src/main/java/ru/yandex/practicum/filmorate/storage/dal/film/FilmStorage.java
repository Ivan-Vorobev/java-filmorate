package ru.yandex.practicum.filmorate.storage.dal.film;

import ru.yandex.practicum.filmorate.storage.dal.dto.FilmDto;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {
    Collection<FilmDto> findAll();

    Optional<FilmDto> findById(Long filmId);

    FilmDto add(FilmDto film);

    FilmDto update(FilmDto film);

    void removeFilmById(Long filmId);

    void deleteLike(FilmDto film, Long userId);

    void addLike(FilmDto film, Long userId);

    Set<Long> getFilmLikes(FilmDto film);

    Collection<FilmDto> getAllLikes(Long count, Long genreId, Integer year);

    Collection<FilmDto> getCommonFilms(Long userId, Long friendId);

    Collection<FilmDto> searchFilm(String query, String by);

    Collection<FilmDto> findFilmsByDirectorSortYear(Long directorId);

    Collection<FilmDto> findFilmsByDirectorSortLike(Long directorId);

    Collection<FilmDto> getUserRecommendations(Long userId);
}
