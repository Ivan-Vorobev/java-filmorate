package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.RequestMethod;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.service.FilmService;
import java.util.Collection;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public Collection<FilmDto> findAll() {
        log.info("GET /films");
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public FilmDto findFilmById(
            @PathVariable("id") Long filmId
    ) {
        return filmService.findFilmById(filmId);
    }

    @PostMapping
    @Validated({RequestMethod.Create.class})
    public FilmDto create(@Valid @RequestBody FilmDto film) {
        FilmDto newFilm = filmService.create(film);
        log.info("Add film with id: " + newFilm.getId());
        return newFilm;
    }

    @PutMapping
    @Validated({RequestMethod.Update.class})
    public FilmDto update(@Valid @RequestBody FilmDto film) {
        FilmDto updatedFilm = filmService.update(film);
        log.info("Film updated. Id: " + updatedFilm.getId());
        return updatedFilm;
    }

    @DeleteMapping("/{filmId}")
    public void removeFilmById(@PathVariable("filmId") Long filmId) {
        log.info("Получен запрос DELETE на удаление фильма с id = {}", filmId);
        filmService.removeFilmById(filmId);
    }

    @PutMapping("/{id}/like/{userId}")
    public FilmDto addLike(
            @PathVariable("id") Long filmId,
            @PathVariable("userId") Long userId
    ) {
        return filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public FilmDto deleteLike(
            @PathVariable("id") Long filmId,
            @PathVariable("userId") Long userId
    ) {
        return filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/popular")
    public Collection<FilmDto> findTopPopular(
            @RequestParam(value = "count", defaultValue = "10") Long count,
            @RequestParam(value = "genreId", defaultValue = "0") Long genreId,
            @RequestParam(value = "year", defaultValue = "0") Integer year
    ) {
        return filmService.findTopPopular(count, genreId, year);
    }

    @GetMapping("/director/{id}")
    public Collection<FilmDto> getFilmByDirectorId(
            @PathVariable("id") Long directorId,
            @RequestParam(value = "sortBy") String sortBy
    ) {
        return filmService.findFilmsByDirector(directorId, sortBy);
    }

    @GetMapping("/search")
    public Collection<FilmDto> searchFilm(
            @RequestParam(value = "query") String query,
            @RequestParam(value = "by") String by
    ) {
        return filmService.searchFilm(query, by);
    }

    @GetMapping("/common")
    public Collection<FilmDto> getCommonFilms(
            @RequestParam(value = "userId") Long userId,
            @RequestParam(value = "friendId") Long friendId
    ) {
        return filmService.getCommonFilms(userId, friendId);
    }
}