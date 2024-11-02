package ru.yandex.practicum.filmorate.dal.storage.film;

import ru.yandex.practicum.filmorate.dal.model.Film;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {
    Collection<Film> findAll();

    Optional<Film> findById(Long filmId);

    Film add(Film film);

    Film update(Film film);

    void removeFilmById(Long filmId);

    void deleteLike(Film film, Long userId);

    void addLike(Film film, Long userId);

    Set<Long> getFilmLikes(Film film);

    Collection<Film> getAllLikes(Long count, Long genreId, Integer year);

    Collection<Film> getCommonFilms(Long userId, Long friendId);

    Collection<Film> searchFilm(String query, String by);

    Collection<Film> findFilmsByDirectorSortYear(Long directorId);

    Collection<Film> findFilmsByDirectorSortLike(Long directorId);

    Collection<Film> getUserRecommendations(Long userId);
}
