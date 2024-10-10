package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.RequestMethod;
import ru.yandex.practicum.filmorate.model.Film;
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
    public Collection<Film> findAll() {
        log.info("GET /films");
        return filmService.findAll();
    }

    @PostMapping
    @Validated({RequestMethod.Create.class})
    public Film add(@Valid @RequestBody Film film) {
        Film newFilm = filmService.create(film);
        log.info("Add film with id: " + newFilm.getId());
        return newFilm;
    }

    @PutMapping
    @Validated({RequestMethod.Update.class})
    public Film update(@Valid @RequestBody Film film) {
        Film updatedFilm = filmService.update(film);
        log.info("Film updated. Id: " + updatedFilm.getId());
        return updatedFilm;
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(
            @PathVariable("id") Long filmId,
            @PathVariable("userId") Long userId
    ) {
        return filmService.addLike(filmId, userId);
    }


    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(
            @PathVariable("id") Long filmId,
            @PathVariable("userId") Long userId
    ) {
        return filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopular(
            @RequestParam(value = "count", defaultValue = "10") Long count
    ) {
        return filmService.findTopPopular(count);
    }
}
