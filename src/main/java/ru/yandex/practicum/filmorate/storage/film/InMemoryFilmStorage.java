package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.GenerateIdStorage;

import java.util.*;

@RequiredArgsConstructor
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private final Map<Long, Set<Long>> likes = new HashMap<>();
    private final GenerateIdStorage idGenerator;

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film findById(Long filmId) {
        return films.get(filmId);
    }

    @Override
    public Film add(Film film) {
        film.setId(idGenerator.generate());
        films.put(film.getId(), film);
        likes.put(film.getId(), new HashSet<>());
        return film;
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void addLike(Film film, Long userId) {
        Set<Long> filmLikes = this.getFilmLikes(film);
        filmLikes.add(userId);
    }

    @Override
    public void deleteLike(Film film, Long userId) {
        Set<Long> filmLikes = this.getFilmLikes(film);
        filmLikes.remove(userId);
    }

    @Override
    public Set<Long> getFilmLikes(Film film) {
        return likes.get(film.getId());
    }

    @Override
    public Map<Long, Set<Long>> getAllLikes() {
        return likes;
    }
}
