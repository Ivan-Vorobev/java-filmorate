package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseStorage;
import ru.yandex.practicum.filmorate.dal.dto.FilmGenreDto;
import ru.yandex.practicum.filmorate.dal.dto.GenreDto;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class GenreDbStorage implements GenreStorage {
    private static final String FIND_GENRE_QUERY = "SELECT * FROM genre WHERE id = ?";
    private static final String FIND_ALL_GENRE_QUERY = "SELECT * FROM genre";
    private static final String FIND_ALL_FILM_GENRE_QUERY = "SELECT * FROM film_genres";
    private static final String FIND_FILM_GENRE_QUERY = "SELECT * FROM film_genres WHERE film_id = ?";
    private static final String INSERT_GENRE_QUERY = "INSERT INTO film_genres (film_id, genre_id) " +
            "VALUES (?, ?)";
    private static final String DELETE_GENRE_QUERY = "DELETE FROM film_genres WHERE film_id = ?";

    private final BaseStorage<GenreDto> genreBaseStorage;
    private final BaseStorage<FilmGenreDto> filmGenreBaseStorage;

    @Autowired
    public GenreDbStorage(
            JdbcTemplate jdbc,
            RowMapper<GenreDto> genreDtoRowMapper,
            RowMapper<FilmGenreDto> filmGenreDtoRowMapper
    ) {
        genreBaseStorage = new BaseStorage<>(jdbc, genreDtoRowMapper);
        filmGenreBaseStorage = new BaseStorage<>(jdbc, filmGenreDtoRowMapper);
    }

    @Override
    public Collection<GenreDto> findAll() {
        return genreBaseStorage.findMany(FIND_ALL_GENRE_QUERY);
    }

    @Override
    public Optional<GenreDto> findById(Long genreId) {
        return genreBaseStorage.findOne(FIND_GENRE_QUERY, genreId);
    }

    @Override
    public Set<Long> findFilmGenres(Long filmId) {
        return filmGenreBaseStorage.findMany(FIND_FILM_GENRE_QUERY, filmId).stream()
                .map(FilmGenreDto::getGenreId)
                .collect(Collectors.toSet());
    }

    @Override
    public Map<Long, Set<Long>> findAllFilmGenres() {
        Map<Long, Set<Long>> result = new HashMap<>();

        for (FilmGenreDto filmGenre : filmGenreBaseStorage.findMany(FIND_ALL_FILM_GENRE_QUERY)) {
            Set<Long> filmGenres = result.computeIfAbsent(filmGenre.getFilmId(), k -> new HashSet<>());
            filmGenres.add(filmGenre.getGenreId());
        }

        return result;
    }

    @Override
    public void delete(Long filmId) {
        genreBaseStorage.delete(
                DELETE_GENRE_QUERY,
                filmId
        );
    }

    @Override
    public FilmGenreDto add(Long filmId, Long genreId) {
        try {
            genreBaseStorage.insert(
                    INSERT_GENRE_QUERY,
                    filmId,
                    genreId
            );
        } catch (DuplicateKeyException ignored) {
        }

        return FilmGenreDto.builder()
                .genreId(genreId)
                .filmId(filmId)
                .build();
    }
}
