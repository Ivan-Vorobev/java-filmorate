package ru.yandex.practicum.filmorate.dal.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.storage.GenerateIdStorage;
import ru.yandex.practicum.filmorate.dal.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> memoryFilms = new HashMap<>();
    private final Map<Long, Set<Long>> likes = new HashMap<>();
    private final GenerateIdStorage idGenerator;

    @Override
    public Collection<Film> findAll() {
        return memoryFilms.values();
    }

    @Override
    public Collection<Film> findFilmsByDirectorSortYear(Long directorId) {
        return List.of();
    }

    @Override
    public Collection<Film> findFilmsByDirectorSortLike(Long directorId) {
        return List.of();
    }

    @Override
    public Optional<Film> findById(Long filmId) {
        Film film = memoryFilms.get(filmId);
        return film == null
                ? Optional.empty()
                : Optional.of(film);
    }

    @Override
    public Film add(Film film) {
        film.setId(idGenerator.generate());
        memoryFilms.put(film.getId(), film);
        likes.put(film.getId(), new HashSet<>());
        return film;
    }

    @Override
    public Film update(Film film) {
        memoryFilms.put(film.getId(), film);
        return film;
    }

    @Override
    public void removeFilmById(Long filmId) {

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
    public Collection<Film> getAllLikes(Long count, Long genreId, Integer year) {
        return null;
    }

    @Override
    public Collection<Film> getUserRecommendations(Long userId) {
        return null;
    }

    @Override
    public Collection<Film> searchFilm(String query, String by) {
        return List.of();
    }

    @Override
    public Collection<Film> getCommonFilms(Long userId, Long friendId) {
        List<Film> films = new ArrayList<>();

        List<Long> filmIds = likes.entrySet().stream()
                .filter(map -> map.getValue().contains(userId) && map.getValue().contains(friendId))
                .collect(Collectors
                        .toMap(Map.Entry::getKey, entry -> entry.getValue().size()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .toList();

        for (Long filmId : filmIds) {
            films.add(memoryFilms.get(filmId));
        }

        return films;
    }
}