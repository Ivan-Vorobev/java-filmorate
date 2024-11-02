package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.constraint.SortFilm;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.dal.model.Film;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.service.enums.EventType;
import ru.yandex.practicum.filmorate.service.mapper.FilmDtoMapper;
import ru.yandex.practicum.filmorate.dal.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.dal.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.dal.storage.genre.GenreStorage;

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

    public Collection<FilmDto> findAll() {
        return FilmDtoMapper.dtoFromModel(filmStorage.findAll());
    }

    public FilmDto findFilmById(Long filmId) {
        Film film = filmStorage
                .findById(filmId)
                .orElseThrow(() -> new NotFoundException("Film not found. Id: " + filmId));
        return FilmDtoMapper.dtoFromModel(film);
    }

    public FilmDto create(FilmDto film) {
        validateFilm(film);

        FilmDto newFilm = FilmDtoMapper.dtoFromModel(
                filmStorage.add(
                        FilmDtoMapper.modelFromDto(film)
                )
        );

        if (film.getMpa() != null) {
            newFilm.setMpa(ratingService.findRatingById(film.getMpa().getId()));
        }

        if (film.getGenres() != null) {
            Collection<GenreDto> newGenres = new ArrayList<>();
            Map<Long, GenreDto> genres = genreService.findAllAsMap();

            for (GenreDto genreDto : film.getGenres()) {
                genreStorage.add(newFilm.getId(), genreDto.getId());
                newGenres.add(genres.get(genreDto.getId()));
            }

            newFilm.setGenres(newGenres);
        }

        if (film.getDirectors() != null) {
            Collection<DirectorDto> newDirectors = new ArrayList<>();
            Map<Long, DirectorDto> directors = directorService.findAll().stream()
                    .collect(Collectors.toMap(DirectorDto::getId, director -> director));

            for (DirectorDto director : film.getDirectors()) {
                directorStorage.addFilm(newFilm.getId(), director.getId());
                newDirectors.add(directors.get(director.getId()));
            }

            newFilm.setDirectors(newDirectors);
        }

        return newFilm;
    }

    public FilmDto update(FilmDto film) {
        findFilmById(film.getId());
        validateFilm(film);

        FilmDto updatedFilm = FilmDtoMapper.dtoFromModel(
                filmStorage.update(
                        FilmDtoMapper.modelFromDto(film)
                )
        );

        if (film.getMpa() != null) {
            updatedFilm.setMpa(ratingService.findRatingById(film.getMpa().getId()));
        }

        if (film.getGenres() != null) {
            genreStorage.delete(updatedFilm.getId());

            HashMap<Long, Boolean> newGenreIds = new HashMap<>();
            Collection<GenreDto> genreDtos = genreService.findAll();
            for (GenreDto genreDto : film.getGenres()) {
                genreStorage.add(updatedFilm.getId(), genreDto.getId());
                newGenreIds.put(genreDto.getId(), true);
            }
            updatedFilm.setGenres(
                    genreDtos.stream()
                            .filter(g -> newGenreIds.get(g.getId()) != null)
                            .toList()
            );
        } else {
            genreStorage.delete(updatedFilm.getId());
        }

        if (film.getDirectors() != null) {
            directorStorage.deleteByFilm(updatedFilm.getId());
            updatedFilm.setDirectors(film.getDirectors());

            for (DirectorDto director : film.getDirectors()) {
                directorStorage.addFilm(updatedFilm.getId(), director.getId());
            }
        } else {
            directorStorage.deleteByFilm(updatedFilm.getId());
        }

        return updatedFilm;
    }

    public void removeFilmById(Long filmId) {
        FilmDto film = findFilmById(filmId);
        filmStorage.removeFilmById(film.getId());
    }

    public FilmDto addLike(final Long filmId, final Long userId) {
        userService.getUserById(userId);
        FilmDto film = findFilmById(filmId);
        filmStorage.addLike(FilmDtoMapper.modelFromDto(film), userId);
        eventService.add(filmId, userId, EventType.LIKE);
        return film;
    }

    public FilmDto deleteLike(final Long filmId, final Long userId) {
        userService.getUserById(userId);
        FilmDto film = findFilmById(filmId);
        filmStorage.deleteLike(FilmDtoMapper.modelFromDto(film), userId);
        eventService.remove(filmId, userId, EventType.LIKE);
        return film;
    }

    public Collection<FilmDto> findTopPopular(final Long topCount, Long genreId, Integer year) {
        if (topCount < 1) {
            throw new ValidationException("The 'count' value must be greater than 0, " + topCount + " given");
        }

        return FilmDtoMapper.dtoFromModel(filmStorage.getAllLikes(topCount, genreId, year));
    }

    public Collection<FilmDto> findFilmsByDirector(final Long directorId, final String sortBy) {
        directorService.findDirectorByID(directorId);
        return switch (SortFilm.fromString(sortBy)) {
            case LIKES -> FilmDtoMapper.dtoFromModel(filmStorage.findFilmsByDirectorSortLike(directorId));
            case YEAR -> FilmDtoMapper.dtoFromModel(filmStorage.findFilmsByDirectorSortYear(directorId));
            default -> throw new NotFoundException("Incorrect sort type: " + sortBy);
        };
    }

    public Collection<FilmDto> getRecommendations(Long userId) {
        userService.getUserById(userId);

        return FilmDtoMapper.dtoFromModel(filmStorage.getUserRecommendations(userId));
    }

    public Collection<FilmDto> getCommonFilms(Long userId, Long friendId) {
        UserDto userDto = userService.getUserById(userId);
        UserDto friend = userService.getUserById(friendId);
        return FilmDtoMapper.dtoFromModel(filmStorage.getCommonFilms(userDto.getId(), friend.getId()));
    }

    private void validateFilm(FilmDto film) {
        if (film.getMpa() != null) {
            try {
                ratingService.findRatingById(film.getMpa().getId());
            } catch (Exception e) {
                throw new BadRequestException(e.getMessage());
            }
        }

        if (film.getGenres() != null) {
            Map<Long, GenreDto> genres = genreService.findAllAsMap();
            for (GenreDto genreDto : film.getGenres()) {
                if (genres.get(genreDto.getId()) == null) {
                    throw new BadRequestException("Genre not found. Id: " + genreDto.getId());
                }
            }
        }
    }

    public Collection<FilmDto> searchFilm(String query, String by) {
        return FilmDtoMapper.dtoFromModel(filmStorage.searchFilm(query, by));
    }
}
