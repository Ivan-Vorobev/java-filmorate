package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.dto.FilmDto;
import ru.yandex.practicum.filmorate.storage.GenerateIdStorage;

import java.util.*;

@RequiredArgsConstructor
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, FilmDto> films = new HashMap<>();
    private final Map<Long, Set<Long>> likes = new HashMap<>();
    private final GenerateIdStorage idGenerator;

    @Override
    public Collection<FilmDto> findAll() {
        return films.values();
    }

    @Override
    public Optional<FilmDto> findById(Long filmId) {
        FilmDto film = films.get(filmId);
        return film == null
                ? Optional.empty()
                : Optional.of(film);
    }

    @Override
    public FilmDto add(FilmDto film) {
        film.setId(idGenerator.generate());
        films.put(film.getId(), film);
        likes.put(film.getId(), new HashSet<>());
        return film;
    }

    @Override
    public FilmDto update(FilmDto film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void addLike(FilmDto film, Long userId) {
        Set<Long> filmLikes = this.getFilmLikes(film);
        filmLikes.add(userId);
    }

    @Override
    public void deleteLike(FilmDto film, Long userId) {
        Set<Long> filmLikes = this.getFilmLikes(film);
        filmLikes.remove(userId);
    }

    @Override
    public Set<Long> getFilmLikes(FilmDto film) {
        return likes.get(film.getId());
    }

    @Override
    public Map<Long, Set<Long>> getAllLikes() {
        return likes;
    }
}
