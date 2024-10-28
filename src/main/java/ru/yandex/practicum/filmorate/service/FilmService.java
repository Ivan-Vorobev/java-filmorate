package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.constraints.SortFilm;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.dal.dto.FilmDto;
import ru.yandex.practicum.filmorate.exceptions.BadRequestException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final GenreStorage genreStorage;
    private final GenreService genreService;
    private final RatingService ratingService;
    private final DirectorService directorService;
    private final DirectorStorage directorStorage;

    @Autowired
    public FilmService(
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            UserService userService,
            GenreStorage genreStorage,
            GenreService genreService,
            RatingService ratingService,
            DirectorService directorService,
            DirectorStorage directorStorage
    ) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.genreStorage = genreStorage;
        this.genreService = genreService;
        this.ratingService = ratingService;
        this.directorService = directorService;
        this.directorStorage = directorStorage;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll().stream()
                .map(FilmMapper::modelFromDto)
                .toList();
//
//        Map<Long, Set<Long>> filmGenres = genreStorage.findAllFilmGenres();
//        Map<Long, Genre> genres = genreService.findAllAsMap();
//        Map<Long, Rating> ratings = ratingService.findAllAsMap();
//        return filmStorage.findAll().stream()
//                .map(v -> {
//                    Film film = FilmMapper.modelFromDto(v);
//                    Set<Long> currentFilmGenres = filmGenres.get(film.getId());
//                    if (currentFilmGenres != null) {
//                        film.setGenres(
//                                currentFilmGenres.stream()
//                                        .map(genres::get)
//                                        .collect(Collectors.toList())
//                        );
//                    } else {
//                        film.setGenres(List.of());
//                    }
//
//                    if (film.getMpa() != null) {
//                        film.setMpa(
//                                ratings.get(film.getMpa().getId())
//                        );
//                    }
//                    return film;
//                })
//                .collect(Collectors.toList());
    }

    public Film findFilm(Long filmId) {
        FilmDto filmDto = filmStorage
                .findById(filmId)
                .orElseThrow(() -> new NotFoundException("Film not found. Id: " + filmId));
        return FilmMapper.modelFromDto(filmDto);
//        film.setGenres(dto.getGenres().stream()
//                .map(GenreMapper::modelFromDto)
//                .toList());
//        return
//        Map<Long, Genre> genres = genreService.findAllAsMap();
//
//        Film film = FilmMapper.modelFromDto(filmDto);
//        Set<Long> filmGenres = genreStorage.findFilmGenres(film.getId());
//        if (filmGenres != null) {
//            film.setGenres(
//                    filmGenres.stream()
//                            .map(genres::get)
//                            .collect(Collectors.toList())
//            );
//        } else {
//            film.setGenres(List.of());
//        }
//
//        if (film.getMpa() != null) {
//            film.setMpa(ratingService.findRating(film.getMpa().getId()));
//        }
//        return film;
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

    public Film addLike(final Long filmId, final Long userId) {
        userService.findUser(userId);
        Film film = findFilm(filmId);
        filmStorage.addLike(FilmMapper.dtoFromModel(film), userId);

        return film;
    }

    public Film deleteLike(final Long filmId, final Long userId) {
        userService.findUser(userId);
        Film film = findFilm(filmId);
        filmStorage.deleteLike(FilmMapper.dtoFromModel(film), userId);
        return film;
    }

    public Collection<Film> findTopPopular(final Long topCount) {
        if (topCount < 1) {
            throw new ValidationException("Count grater than 0");
        }

        return filmStorage.getAllLikes().entrySet().stream()
                .sorted((v1, v2) -> Integer.compare(v1.getValue().size(), v2.getValue().size()) * -1)
                .limit(topCount)
                .map(v -> findFilm(v.getKey()))
                .collect(Collectors.toList());
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
}
