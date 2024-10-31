package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.dal.BaseStorage;
import ru.yandex.practicum.filmorate.storage.dal.dto.*;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private static final String FIND_ALL_QUERY = """
            SELECT
                f.*,
                r.name as rating_name,
            FROM films f
            LEFT JOIN rating r ON r.id = f.rating_id
            """;
    private static final String FIND_BY_ID_QUERY = """
            SELECT
                f.*,
                r.name as rating_name,
            FROM films f
            LEFT JOIN rating r ON r.id = f.rating_id
            WHERE f.id = ?
            """;
    private static final String FIND_SIMILAR_USER_QUERY = "SELECT l.* " +
            "FROM film_likes AS l " +
            "WHERE l.film_id IN (" +
            "SELECT film_id " +
            "FROM film_likes l1 " +
            "WHERE l1.user_id = ?) " +
            "AND l.user_id <> ? " +
            "GROUP BY l.user_id, l.film_id " +
            "ORDER BY COUNT(l.film_id) DESC " +
            "LIMIT 1";
    private static final String FIND_RECOMMENDED_FILMS_QUERY = """
            SELECT
                f.*,
                r.name as rating_name
            FROM films f
            LEFT JOIN rating r ON r.id = f.rating_id
            LEFT JOIN film_likes l ON f.id = l.film_id
            WHERE l.user_id = ? AND f.id NOT IN (
                SELECT film_id FROM film_likes WHERE user_id = ?
            )
            """;

    private static final String INSERT_FILM_QUERY = "INSERT INTO films (rating_id, name, description, release_date, duration)" +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_FILM_QUERY = """
            UPDATE films SET rating_id = ?, name = ?, description = ?, release_date = ?, duration = ? WHERE id = ?
            """;
    private static final String GET_COMMON_FILMS = """
            SELECT f.*,
                   r.name AS rating_name
            FROM films AS f
            INNER JOIN film_likes AS fl ON fl.film_id = f.id
            LEFT JOIN rating r ON r.id = f.rating_id
            WHERE f.id IN
                (SELECT fl1.film_id
                 FROM film_likes AS fl1
                 INNER JOIN film_likes fl2 ON fl2.film_id = fl1.film_id
                 AND fl1.user_id = ?
                 AND fl2.user_id = ?)
            GROUP BY f.id
            ORDER BY COUNT(fl.user_id) DESC
            """;
    private static final String REMOVE_QUERY = """
            DELETE FROM films
            WHERE id = ?
            """;
    private static final String INSERT_LIKE_QUERY = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
    private static final String FIND_FILM_LIKES_QUERY = "SELECT * FROM film_likes WHERE film_id = ?";
    private static final String FIND_FILMS_BY_DIRECTOR_SORT_LIKE = """
            SELECT DISTINCT
                f.*,
                r.name as rating_name,
                COUNT(*) OVER (PARTITION BY f.id) as count_likes
            FROM films f
            JOIN film_director fd ON fd.film_id = f.id
            LEFT JOIN rating r ON r.id = f.rating_id
            WHERE fd.director_id = ?
            ORDER BY count_likes
            """;

    private static final String FIND_FILMS_BY_DIRECTOR_SORT_YEAR = """
            SELECT
                f.*,
                r.name as rating_name,
            FROM films f
            JOIN film_director fd ON fd.film_id = f.id
            LEFT JOIN rating r ON r.id = f.rating_id
            WHERE fd.director_id = ?
            ORDER BY EXTRACT(YEAR FROM release_date)
            """;

    private static final String SEARCH_QUERY = """
            SELECT
                f.*,
                r.name as rating_name,
            FROM films f
            LEFT JOIN FILM_LIKES fl ON f.ID = fl.FILM_ID
            LEFT JOIN rating r ON r.id = f.rating_id
            WHERE %s
            GROUP BY f.ID
            ORDER BY COALESCE(COUNT(fl.FILM_ID), 0) DESC
            """;
    private static final String FIND_MOST_POPULAR_QUERY = """
            SELECT
                f.*,
                r.name as rating_name,
            FROM films f
            LEFT JOIN FILM_LIKES fl ON f.ID = fl.FILM_ID
            LEFT JOIN film_genres fj ON fj.film_id = f.id
            LEFT JOIN rating r ON r.id = f.rating_id
            WHERE %s
            GROUP BY f.ID
            ORDER BY COUNT(fl.FILM_ID) DESC
            """;
    private static final String FIND_FILM_GENRES = """
            SELECT
                fg.film_id AS film_id,
                fg.genre_id AS genre_id,
                g.name AS genre_name
            FROM film_genres fg
            INNER JOIN genre g ON g.id = fg.genre_id
            WHERE fg.film_id IN (%s)
            ORDER BY fg.film_id ASC
            """;
    private static final String FIND_FILM_DIRECTORS = """
            SELECT
                fd.film_id AS film_id,
                fd.director_id AS director_id,
                d.name AS director_name
            FROM film_director fd
            INNER JOIN director d ON d.id = fd.director_id
            WHERE fd.film_id IN (%s)
            ORDER BY fd.film_id ASC
            """;

    private final BaseStorage<FilmDto> filmBaseStorage;
    private final BaseStorage<FilmLikesDto> filmLikesBaseStorage;
    private final BaseStorage<FilmFullGenreDto> filmGenreBaseStorage;
    private final BaseStorage<FilmFullDirectorDto> filmDirectorBaseStorage;


    @Autowired
    public FilmDbStorage(
            JdbcTemplate jdbc,
            RowMapper<FilmDto> filmDtoRowMapper,
            RowMapper<FilmLikesDto> filmLikesRowMapper,
            RowMapper<FilmFullGenreDto> filmGenreRowMapper,
            RowMapper<FilmFullDirectorDto> filmDirectorRowMapper
    ) {
        filmBaseStorage = new BaseStorage<>(jdbc, filmDtoRowMapper);
        filmLikesBaseStorage = new BaseStorage<>(jdbc, filmLikesRowMapper);
        filmGenreBaseStorage = new BaseStorage<>(jdbc, filmGenreRowMapper);
        filmDirectorBaseStorage = new BaseStorage<>(jdbc, filmDirectorRowMapper);
    }

    @Override
    public Collection<FilmDto> findAll() {
        return prepareFilmDtoData(filmBaseStorage.findMany(FIND_ALL_QUERY));
    }

    @Override
    public Optional<FilmDto> findById(Long filmId) {
        Collection<FilmDto> films = prepareFilmDtoData(filmBaseStorage.findMany(FIND_BY_ID_QUERY, filmId));
        Optional<FilmDto> film = films.stream().findFirst();

        if (film.isPresent()) {
            loadGenres(List.of(film.get()));
            loadDirectors(List.of(film.get()));
        }

        return film;
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
    public void removeFilmById(Long filmId) {
        filmBaseStorage.delete(REMOVE_QUERY, filmId);
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
    public Collection<FilmDto> getAllLikes(Long count, Long genreId, Integer year) {
        StringBuilder conditions = new StringBuilder("1=1");
        List<Object> params = new ArrayList<>();

        if (genreId != 0) {
            conditions.append(" AND fj.genre_id = ?");
            params.add(genreId);
        }
        if (year != 0) {
            conditions.append(" AND YEAR(f.release_date) = ?");
            params.add(year);
        }

        String query = String.format(FIND_MOST_POPULAR_QUERY + " LIMIT ?", conditions);
        params.add(count);


        return prepareFilmDtoData(filmBaseStorage.findMany(query, params.toArray()));
    }

    @Override
    public Collection<FilmDto> findFilmsByDirectorSortYear(Long directorId) {
        return prepareFilmDtoData(filmBaseStorage.findMany(FIND_FILMS_BY_DIRECTOR_SORT_YEAR, directorId));
    }

    @Override
    public Collection<FilmDto> findFilmsByDirectorSortLike(Long directorId) {
        return prepareFilmDtoData(filmBaseStorage.findMany(FIND_FILMS_BY_DIRECTOR_SORT_LIKE, directorId));
    }

    @Override
    public Collection<FilmDto> searchFilm(String query, String by) {
        List<Object> params = new ArrayList<>();
        StringBuilder conditions = new StringBuilder();

        for (String condition : by.split(",")) {
            SearchByEnum.fromString(condition).ifPresent(searchBy -> {
                if (!conditions.isEmpty()) {
                    conditions.append(" OR ");
                }
                switch (searchBy) {
                    case TITLE -> {
                        conditions.append("f.name LIKE ?");
                        params.add("%" + query + "%");
                    }
                    case DIRECTOR -> {
                        conditions.append("""
                                f.id IN (
                                    SELECT DISTINCT fd.film_id
                                    FROM film_director fd
                                    INNER JOIN director d ON d.id = fd.director_id
                                    WHERE d.name LIKE ?
                                )
                                """);
                        params.add("%" + query + "%");
                    }
                }
            });
        }
        String formattedQuery = String.format(SEARCH_QUERY, conditions);
        return prepareFilmDtoData(filmBaseStorage.findMany(formattedQuery, params.toArray()));
    }

    @Override
    public Collection<FilmDto> getCommonFilms(Long userId, Long friendId) {
        return prepareFilmDtoData(filmBaseStorage.findMany(GET_COMMON_FILMS, userId, friendId));
    }

    private Collection<FilmDto> prepareFilmDtoData(Collection<FilmDto> films) {
        loadGenres(films);
        loadDirectors(films);
        return films;
    }

    private void loadGenres(Collection<FilmDto> films) {
        if (films == null || films.isEmpty()) {
            return;
        }

        HashMap<Long, Collection<GenreDto>> filmGenres = new HashMap<>();

        Collection<FilmFullGenreDto> findFilmGenres = filmGenreBaseStorage.findMany(
                FIND_FILM_GENRES.formatted(
                        Strings.join(Arrays.stream(new StringBuilder()
                                .repeat("?", films.size())
                                .toString()
                                .split("")).toList(), ',')),
                films.stream().map(v -> v.getId().toString()).toArray()
        );

        for (FilmFullGenreDto filmFullGenreDto : findFilmGenres) {
            Collection<GenreDto> genres = filmGenres.get(filmFullGenreDto.getFilmId());
            if (genres == null) {
                genres = new ArrayList<>();
                filmGenres.put(filmFullGenreDto.getFilmId(), genres);
            }

            genres.add(
                    GenreDto.builder()
                            .id(filmFullGenreDto.getGenreId())
                            .name(filmFullGenreDto.getGenreName())
                            .build()
            );
        }

        for (FilmDto film : films) {
            Collection<GenreDto> genres = filmGenres.get(film.getId());
            if (genres != null) {
                film.setGenres(genres);
            }
        }
    }

    private void loadDirectors(Collection<FilmDto> films) {
        if (films == null || films.isEmpty()) {
            return;
        }

        HashMap<Long, Collection<DirectorDto>> filmDirectors = new HashMap<>();

        Collection<FilmFullDirectorDto> findFilmDirectors = filmDirectorBaseStorage.findMany(
                FIND_FILM_DIRECTORS.formatted(
                        Strings.join(Arrays.stream(new StringBuilder()
                                .repeat("?", films.size())
                                .toString()
                                .split("")).toList(), ',')
                ),
                films.stream().map(FilmDto::getId).toArray()
        );

        for (FilmFullDirectorDto findFilmDirector : findFilmDirectors) {
            Collection<DirectorDto> directors = filmDirectors.get(findFilmDirector.getFilmId());
            if (directors == null) {
                directors = new ArrayList<>();
                filmDirectors.put(findFilmDirector.getFilmId(), directors);
            }

            directors.add(
                    DirectorDto.builder()
                            .id(findFilmDirector.getDirectorId())
                            .name(findFilmDirector.getDirectorName())
                            .build()
            );
        }

        for (FilmDto film : films) {
            Collection<DirectorDto> directors = filmDirectors.get(film.getId());
            if (directors != null) {
                film.setDirectors(directors);
            }
        }
    }

    public Collection<FilmDto> getUserRecommendations(Long userId) {
        Optional<Long> similarUserOptional = filmLikesBaseStorage
                .findOne(FIND_SIMILAR_USER_QUERY, userId, userId)
                .map(FilmLikesDto::getUserId);

        if (similarUserOptional.isEmpty()) {
            return Collections.emptyList();
        }
        Long similarUserId = similarUserOptional.get();

        // Получаем список фильмов, которые понравились похожему пользователю, но отсутствуют у текущего пользователя
        Collection<FilmDto> recommendedFilms = filmBaseStorage.findMany(FIND_RECOMMENDED_FILMS_QUERY, similarUserId, userId);

        return prepareFilmDtoData(recommendedFilms);
    }
}
