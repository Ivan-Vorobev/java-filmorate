package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.GenerateIdService;

import java.util.*;

@RequiredArgsConstructor
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private final Map<Long, Set<Long>> likes = new HashMap<>();
    private final GenerateIdService idGenerator;

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film findById(Long filmId) {
        Film film = films.get(filmId);

        if (film == null) {
            throw new NotFoundException("Film not found. Id: " + filmId);
        }

        return film;
    }

    @Override
    public Film add(Film film) {
        film.setId(idGenerator.generate());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        Film searchFilm = films.get(film.getId());

        if (searchFilm == null) {
            throw new NotFoundException("Film not found. Id: " + film.getId());
        }

        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void addLike(Film film, Long userId) {
        if (film == null) {
            throw new NotFoundException("film is null");
        }
        Set<Long> filmLikes = this.getFilmLikes(film);
        filmLikes.add(userId);
    }

    @Override
    public void deleteLike(Film film, Long userId) {
        if (film == null) {
            throw new NotFoundException("film is null");
        }
        Set<Long> filmLikes = this.getFilmLikes(film);
        filmLikes.remove(userId);
    }

    @Override
    public Set<Long> getFilmLikes(Film film) {
        Set<Long> filmLikes = likes.get(film.getId());

        if (filmLikes == null) {
            filmLikes = new HashSet<>();
            likes.put(film.getId(), filmLikes);
        }

        return filmLikes;
    }

    @Override
    public Map<Long, Set<Long>> getAllLikes() {
        return likes;
    }
}
