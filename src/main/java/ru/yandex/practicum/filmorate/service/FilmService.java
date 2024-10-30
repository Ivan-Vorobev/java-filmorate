package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.constraints.SortFilm;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.storage.dal.dto.FilmDto;
import ru.yandex.practicum.filmorate.exceptions.BadRequestException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final GenreStorage genreStorage;
    private final GenreService genreService;
    private final RatingService ratingService;
    private final DirectorService directorService;
    private final DirectorStorage directorStorage;
    private final EventService eventService;

    @Autowired
    public FilmService(
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            UserService userService,
            GenreStorage genreStorage,
            GenreService genreService,
            RatingService ratingService,
            DirectorService directorService,
            DirectorStorage directorStorage,
            EventService eventService
    ) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.genreStorage = genreStorage;
        this.genreService = genreService;
        this.ratingService = ratingService;
        this.directorService = directorService;
        this.directorStorage = directorStorage;
        this.eventService = eventService;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll().stream()
                .map(FilmMapper::modelFromDto)
                .toList();
    }

    public Film findFilm(Long filmId) {
        FilmDto filmDto = filmStorage
                .findById(filmId)
                .orElseThrow(() -> new NotFoundException("Film not found. Id: " + filmId));
        return FilmMapper.modelFromDto(filmDto);
    }

    public Film create(Film film) {
        validateFilm(film);

        Film newFilm = FilmMapper.modelFromDto(
                filmStorage.add(
                        FilmMapper.dtoFromModel(film)
                )
        );

        if (film.getMpa() != null) {
            newFilm.setMpa(ratingService.findRating(film.getMpa().getId()));
        }

        if (film.getGenres() != null) {
            Collection<Genre> newGenres = new ArrayList<>();
            Map<Long, Genre> genres = genreService.findAllAsMap();

            for (Genre genre : film.getGenres()) {
                genreStorage.add(newFilm.getId(), genre.getId());
                newGenres.add(genres.get(genre.getId()));
            }

            newFilm.setGenres(newGenres);
        }

        if (film.getDirectors() != null) {
            Collection<Director> newDirectors = new ArrayList<>();
            Map<Long, Director> directors = directorService.findAllAsMap();

            for (Director director : film.getDirectors()) {
                directorStorage.addFilm(newFilm.getId(), director.getId());
                newDirectors.add(directors.get(director.getId()));
            }

            newFilm.setDirectors(newDirectors);
        }

        return newFilm;
    }

    public Film update(Film film) {
        findFilm(film.getId());
        validateFilm(film);

        Film updatedFilm = FilmMapper.modelFromDto(
                filmStorage.update(
                        FilmMapper.dtoFromModel(film)
                )
        );

        if (film.getMpa() != null) {
            updatedFilm.setMpa(ratingService.findRating(film.getMpa().getId()));
        }

        if (film.getGenres() != null) {
            genreStorage.delete(updatedFilm.getId());

            for (Genre genre : film.getGenres()) {
                genreStorage.add(updatedFilm.getId(), genre.getId());
            }
            updatedFilm.setGenres(film.getGenres());
        } else {
            genreStorage.delete(updatedFilm.getId());
        }

        if (film.getDirectors() != null) {
            directorStorage.deleteByFilm(updatedFilm.getId());
            updatedFilm.setDirectors(film.getDirectors());

            for (Director director : film.getDirectors()) {
                directorStorage.addFilm(updatedFilm.getId(), director.getId());
            }
        } else {
            directorStorage.deleteByFilm(updatedFilm.getId());
        }

        return updatedFilm;
    }

    public void removeFilmById(Long filmId) {
        Film film = findFilm(filmId);
        filmStorage.removeFilmById(film.getId());
    }

    public Film addLike(final Long filmId, final Long userId) {
        userService.findUser(userId);
        Film film = findFilm(filmId);
        filmStorage.addLike(FilmMapper.dtoFromModel(film), userId);
        eventService.add(filmId, userId, EventType.LIKE);
        return film;
    }

    public Film deleteLike(final Long filmId, final Long userId) {
        userService.findUser(userId);
        Film film = findFilm(filmId);
        filmStorage.deleteLike(FilmMapper.dtoFromModel(film), userId);
        eventService.remove(filmId, userId, EventType.LIKE);
        return film;
    }

    public Collection<Film> findTopPopular(final Long topCount, Long genreId, Integer year) {
        if (topCount < 1) {
            throw new ValidationException("The 'count' value must be greater than 0, " + topCount + " given");
        }

        return filmStorage.getAllLikes(topCount, genreId, year).stream()
                .map(FilmMapper::modelFromDto)
                .toList();
    }

    public Collection<Film> findFilmsByDirector(final Long directorId, final String sortBy) {
        directorService.findDirector(directorId);
        return switch (SortFilm.fromString(sortBy)) {
            case LIKES -> filmStorage.findFilmsByDirectorSortLike(directorId).stream()
                    .map(FilmMapper::modelFromDto)
                    .toList();
            case YEAR -> filmStorage.findFilmsByDirectorSortYear(directorId).stream()
                    .map(FilmMapper::modelFromDto)
                    .toList();
            default -> throw new NotFoundException("Incorrect sort type: " + sortBy);
        };
    }

    public Collection<Film> getRecommendations(Long userId) {
        userService.findUser(userId);

        return filmStorage.getUserRecommendations(userId).stream()
                .map(FilmMapper::modelFromDto)
                .toList();
    }

    public Collection<Film> getCommonFilms(Long userId, Long friendId) {
        User user = userService.findUser(userId);
        User friend = userService.findUser(friendId);
        return filmStorage.getCommonFilms(user.getId(), friend.getId()).stream()
                .map(FilmMapper::modelFromDto)
                .toList();
    }

    private void validateFilm(Film film) {
        if (film.getMpa() != null) {
            try {
                ratingService.findRating(film.getMpa().getId());
            } catch (Exception e) {
                throw new BadRequestException(e.getMessage());
            }
        }

        if (film.getGenres() != null) {
            Map<Long, Genre> genres = genreService.findAllAsMap();
            for (Genre genre : film.getGenres()) {
                if (genres.get(genre.getId()) == null) {
                    throw new BadRequestException("Genre not found. Id: " + genre.getId());
                }
            }
        }
    }

    public Collection<Film> searchFilm(String query, String by) {
        return filmStorage.searchFilm(query, by).stream()
                .map((FilmMapper::modelFromDto))
                .toList();
    }
}
