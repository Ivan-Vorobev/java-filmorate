package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.GenerateIdStorage;
import ru.yandex.practicum.filmorate.storage.dal.dto.FilmDto;

import java.util.*;
import java.util.stream.Collectors;

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
    public Collection<FilmDto> findFilmsByDirectorSortYear(Long directorId) {
        return List.of();
    }

    @Override
    public Collection<FilmDto> findFilmsByDirectorSortLike(Long directorId) {
        return List.of();
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
    public void removeFilmById(Long filmId) {

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
    public Collection<FilmDto> getAllLikes(Long count, Long genreId, Integer year) {
        return null;
    }

    @Override
    public Collection<FilmDto> getUserRecommendations(Long userId) {
        return null;
    }

    @Override
    public Collection<FilmDto> searchFilm(String query, String by) {
        return List.of();
    }

    @Override
    public Collection<FilmDto> getCommonFilms(Long userId, Long friendId) {
        List<FilmDto> filmsDto = new ArrayList<>();

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
            filmsDto.add(films.get(filmId));
        }

        return filmsDto;
    }
}