package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.RequestMethod;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.services.GenerateIdService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();
    private final GenerateIdService idGenerator = new GenerateIdService();

    @GetMapping
    public Collection<Film> findAll() {
        log.info("GET /films");
        return films.values();
    }

    @PostMapping
    @Validated({RequestMethod.Create.class})
    public Film add(@Valid @RequestBody Film film) {
        log.info("POST /films");
        film.setId(idGenerator.generate());
        films.put(film.getId(), film);
        log.info("Add film with id: " + film.getId());
        return film;
    }

    @PutMapping
    @Validated({RequestMethod.Update.class})
    public Film update(@Valid @RequestBody Film film) {
        log.info("PUT /films");
        Film searchFilm = films.get(film.getId());

        if (searchFilm == null) {
            String errorMessage = "Film not found. Id: " + film.getId();
            log.info(errorMessage);
            throw new NotFoundException(errorMessage);
        }

        searchFilm.setName(film.getName());
        searchFilm.setDescription(film.getDescription());
        searchFilm.setDuration(film.getDuration());
        searchFilm.setReleaseDate(film.getReleaseDate());

        log.info("Film updated. Id: " + film.getId());
        return film;
    }
}
