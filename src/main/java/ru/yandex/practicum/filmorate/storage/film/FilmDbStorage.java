package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.dal.BaseStorage;
import ru.yandex.practicum.filmorate.storage.dal.dto.FilmDto;
import ru.yandex.practicum.filmorate.storage.dal.dto.FilmLikesDto;
import ru.yandex.practicum.filmorate.storage.dal.dto.GenreDto;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class FilmDbStorage implements FilmStorage {
    private static final String FIND_ALL_QUERY = """
            SELECT
                f.*,
                g.id as genre_id,
                g.name as genre_name,
                r.name as rating_name
            FROM films f
            LEFT JOIN film_genres fj ON fj.film_id = f.id
            LEFT JOIN genre g ON g.id = fj.genre_id
            LEFT JOIN rating r ON r.id = f.rating_id
            """;
    private static final String FIND_BY_ID_QUERY = """
            SELECT
                f.*,
                g.id as genre_id,
                g.name as genre_name,
                r.name as rating_name
            FROM films f
            LEFT JOIN film_genres fj ON fj.film_id = f.id
            LEFT JOIN genre g ON g.id = fj.genre_id
            LEFT JOIN rating r ON r.id = f.rating_id
            WHERE f.id = ?
            """;
    private static final String INSERT_FILM_QUERY = "INSERT INTO films (rating_id, name, description, release_date, duration)" +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_FILM_QUERY = """
            UPDATE films SET rating_id = ?, name = ?, description = ?, release_date = ?, duration = ? WHERE id = ?
            """;
    private static final String INSERT_LIKE_QUERY = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
    private static final String FIND_FILM_LIKES_QUERY = "SELECT * FROM film_likes WHERE film_id = ?";
    private static final String FIND_ALL_LIKES_QUERY = "SELECT * FROM film_likes";

    private final BaseStorage<FilmDto> filmBaseStorage;
    private final BaseStorage<FilmLikesDto> filmLikesBaseStorage;


    @Autowired
    public FilmDbStorage(
            JdbcTemplate jdbc,
            RowMapper<FilmDto> filmDtoRowMapper,
            RowMapper<FilmLikesDto> filmLikesRowMapper
    ) {
        filmBaseStorage = new BaseStorage<>(jdbc, filmDtoRowMapper);
        filmLikesBaseStorage = new BaseStorage<>(jdbc, filmLikesRowMapper);
    }

    @Override
    public Collection<FilmDto> findAll() {
        return prepareFilmDtoData(filmBaseStorage.findMany(FIND_ALL_QUERY));
    }

    @Override
    public Optional<FilmDto> findById(Long filmId) {
        Collection<FilmDto> films = prepareFilmDtoData(filmBaseStorage.findMany(FIND_BY_ID_QUERY, filmId));
        return films.stream().findFirst();
    }

    @Override
    public FilmDto add(FilmDto film) {
        Long id = filmBaseStorage.insert(
                INSERT_FILM_QUERY,
                film.getRatingId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration()
        );
        film.setId(id);
        return film;
    }

    @Override
    public FilmDto update(FilmDto film) {
        filmBaseStorage.update(
                UPDATE_FILM_QUERY,
                film.getRatingId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getId()
        );
        return film;
    }

    @Override
    public void deleteLike(FilmDto film, Long userId) {
        filmLikesBaseStorage.delete(DELETE_LIKE_QUERY, film.getId(), userId);
    }

    @Override
    public void addLike(FilmDto film, Long userId) {
        try {
            filmLikesBaseStorage.insert(INSERT_LIKE_QUERY, film.getId(), userId);
        } catch (Exception ignored) {
        }
    }

    @Override
    public Set<Long> getFilmLikes(FilmDto film) {
        return filmLikesBaseStorage.findMany(FIND_FILM_LIKES_QUERY, film.getId()).stream()
                .map(FilmLikesDto::getUserId)
                .collect(Collectors.toSet());
    }

    @Override
    public Map<Long, Set<Long>> getAllLikes() {
        Map<Long, Set<Long>> result = new HashMap<>();

        for (FilmLikesDto userLike : filmLikesBaseStorage.findMany(FIND_ALL_LIKES_QUERY)) {
            Set<Long> filmLikes = result.computeIfAbsent(userLike.getFilmId(), k -> new HashSet<>());
            filmLikes.add(userLike.getUserId());
        }

        return result;
    }

    private Collection<FilmDto> prepareFilmDtoData(Collection<FilmDto> films) {
        HashMap<Long, FilmDto> outputFilms = new HashMap<>();
        for (FilmDto film : films) {
            FilmDto findFilm = outputFilms.get(film.getId());

            if (findFilm == null) {
                findFilm = film;
                outputFilms.put(film.getId(), film);
            }

            if (film.getGenreId() != null && film.getGenreName() != null) {
                Collection<GenreDto> genres = findFilm.getGenres();
                if (genres == null) {
                    genres = new ArrayList<>();
                    findFilm.setGenres(genres);
                }

                genres.add(
                        GenreDto.builder()
                                .id(film.getGenreId())
                                .name(film.getGenreName())
                                .build()
                );
            }
        }

        return outputFilms.values();
    }
}
