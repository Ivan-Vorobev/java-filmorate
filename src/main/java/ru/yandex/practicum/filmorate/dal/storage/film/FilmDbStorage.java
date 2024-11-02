package ru.yandex.practicum.filmorate.dal.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.storage.BaseStorage;
import ru.yandex.practicum.filmorate.dal.model.*;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
@Primary
public class FilmDbStorage implements FilmStorage {
    private static final String FIND_ALL_QUERY = """
            SELECT
                f.*,
                r.name as rating_name,
            FROM films f
            LEFT JOIN rating r ON r.id = f.rating_id
            WHERE f.active = true
            """;
    private static final String FIND_BY_ID_QUERY = """
            SELECT
                f.*,
                r.name as rating_name,
            FROM films f
            LEFT JOIN rating r ON r.id = f.rating_id
            WHERE f.active = true AND f.id = ?
            """;
    private static final String FIND_SIMILAR_USER_QUERY = """
            SELECT l.*
            FROM film_likes AS l
            INNER JOIN films f ON f.id = l.film_id
            WHERE
                f.active = true
                AND l.film_id IN (
                    SELECT l1.film_id
                    FROM film_likes l1
                    WHERE l1.user_id = ?
                )
            AND l.user_id <> ?
            GROUP BY l.user_id, l.film_id
            ORDER BY COUNT(l.film_id) DESC
            LIMIT 1
            """;
    private static final String FIND_RECOMMENDED_FILMS_QUERY = """
            SELECT
                f.*,
                r.name as rating_name
            FROM films f
            LEFT JOIN rating r ON r.id = f.rating_id
            LEFT JOIN film_likes l ON f.id = l.film_id
            WHERE f.active = true AND l.user_id = ? AND f.id NOT IN (
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
                 AND f.active = true
            GROUP BY f.id
            ORDER BY COUNT(fl.user_id) DESC
            """;
    private static final String DEACTIVATE_FILMS = """
            UPDATE films SET active = false WHERE id = ?
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
            WHERE fd.director_id = ? AND f.active = true
            ORDER BY count_likes
            """;

    private static final String FIND_FILMS_BY_DIRECTOR_SORT_YEAR = """
            SELECT
                f.*,
                r.name as rating_name,
            FROM films f
            JOIN film_director fd ON fd.film_id = f.id
            LEFT JOIN rating r ON r.id = f.rating_id
            WHERE fd.director_id = ? AND f.active = true
            ORDER BY EXTRACT(YEAR FROM release_date)
            """;

    private static final String SEARCH_QUERY = """
            SELECT
                f.*,
                r.name as rating_name,
            FROM films f
            LEFT JOIN FILM_LIKES fl ON f.ID = fl.FILM_ID
            LEFT JOIN rating r ON r.id = f.rating_id
            WHERE f.active = true AND ( %s )
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
            WHERE f.active = true %s
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
            INNER JOIN films f ON f.id = fg.film_id
            WHERE fg.film_id IN (%s) AND f.active = true
            ORDER BY fg.film_id ASC
            """;
    private static final String FIND_FILM_DIRECTORS = """
            SELECT
                fd.film_id AS film_id,
                fd.director_id AS director_id,
                d.name AS director_name
            FROM film_director fd
            INNER JOIN director d ON d.id = fd.director_id
            INNER JOIN films f ON f.id = fd.film_id
            WHERE fd.film_id IN (%s) AND f.active = true
            ORDER BY fd.film_id ASC
            """;

    private final BaseStorage<Film> filmBaseStorage;
    private final BaseStorage<FilmLikes> filmLikesBaseStorage;
    private final BaseStorage<FilmFullGenre> filmGenreBaseStorage;
    private final BaseStorage<FilmFullDirector> filmDirectorBaseStorage;


    @Autowired
    public FilmDbStorage(
            JdbcTemplate jdbc,
            RowMapper<Film> filmRowMapper,
            RowMapper<FilmLikes> filmLikesRowMapper,
            RowMapper<FilmFullGenre> filmGenreRowMapper,
            RowMapper<FilmFullDirector> filmDirectorRowMapper
    ) {
        filmBaseStorage = new BaseStorage<>(jdbc, filmRowMapper);
        filmLikesBaseStorage = new BaseStorage<>(jdbc, filmLikesRowMapper);
        filmGenreBaseStorage = new BaseStorage<>(jdbc, filmGenreRowMapper);
        filmDirectorBaseStorage = new BaseStorage<>(jdbc, filmDirectorRowMapper);
    }

    @Override
    public Collection<Film> findAll() {
        return prepareFilmData(filmBaseStorage.findMany(FIND_ALL_QUERY));
    }

    @Override
    public Optional<Film> findById(Long filmId) {
        Collection<Film> films = prepareFilmData(filmBaseStorage.findMany(FIND_BY_ID_QUERY, filmId));
        Optional<Film> film = films.stream().findFirst();

        if (film.isPresent()) {
            loadGenres(List.of(film.get()));
            loadDirectors(List.of(film.get()));
        }

        return film;
    }

    @Override
    public Film add(Film film) {
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
    public Film update(Film film) {
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
    public void deactivateFilmById(Long filmId) {
        filmBaseStorage.update(DEACTIVATE_FILMS, filmId);
    }

    @Override
    public void deleteLike(Film film, Long userId) {
        filmLikesBaseStorage.delete(DELETE_LIKE_QUERY, film.getId(), userId);
    }

    @Override
    public void addLike(Film film, Long userId) {
        try {
            filmLikesBaseStorage.insert(INSERT_LIKE_QUERY, film.getId(), userId);
        } catch (Exception ignored) {
        }
    }

    @Override
    public Set<Long> getFilmLikes(Film film) {
        return filmLikesBaseStorage.findMany(FIND_FILM_LIKES_QUERY, film.getId()).stream()
                .map(FilmLikes::getUserId)
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<Film> getAllLikes(Long count, Long genreId, Integer year) {
        StringBuilder conditions = new StringBuilder("AND 1=1");
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


        return prepareFilmData(filmBaseStorage.findMany(query, params.toArray()));
    }

    @Override
    public Collection<Film> findFilmsByDirectorSortYear(Long directorId) {
        return prepareFilmData(filmBaseStorage.findMany(FIND_FILMS_BY_DIRECTOR_SORT_YEAR, directorId));
    }

    @Override
    public Collection<Film> findFilmsByDirectorSortLike(Long directorId) {
        return prepareFilmData(filmBaseStorage.findMany(FIND_FILMS_BY_DIRECTOR_SORT_LIKE, directorId));
    }

    @Override
    public Collection<Film> searchFilm(String query, String by) {
        List<Object> params = new ArrayList<>();
        StringBuilder conditions = new StringBuilder();

        for (String condition : by.split(",")) {
            SearchByEnum.fromString(condition).ifPresent(searchBy -> {
                if (!conditions.isEmpty()) {
                    conditions.append(" OR ");
                }
                String toLower = query.toLowerCase();
                switch (searchBy) {
                    case TITLE -> {
                        conditions.append("lower(f.name) LIKE ?");
                        params.add("%" + toLower + "%");
                    }
                    case DIRECTOR -> {
                        conditions.append("""
                                f.id IN (
                                    SELECT DISTINCT fd.film_id
                                    FROM film_director fd
                                    INNER JOIN director d ON d.id = fd.director_id
                                    WHERE lower(d.name) LIKE ?
                                )
                                """);
                        params.add("%" + toLower + "%");
                    }
                }
            });
        }
        String formattedQuery = String.format(SEARCH_QUERY, conditions);
        return prepareFilmData(filmBaseStorage.findMany(formattedQuery, params.toArray()));
    }

    @Override
    public Collection<Film> getCommonFilms(Long userId, Long friendId) {
        return prepareFilmData(filmBaseStorage.findMany(GET_COMMON_FILMS, userId, friendId));
    }

    private Collection<Film> prepareFilmData(Collection<Film> films) {
        loadGenres(films);
        loadDirectors(films);
        return films;
    }

    private void loadGenres(Collection<Film> films) {
        if (films == null || films.isEmpty()) {
            return;
        }

        HashMap<Long, Collection<Genre>> filmGenres = new HashMap<>();

        Collection<FilmFullGenre> findFilmGenres = filmGenreBaseStorage.findMany(
                FIND_FILM_GENRES.formatted(
                        Strings.join(Arrays.stream(new StringBuilder()
                                .repeat("?", films.size())
                                .toString()
                                .split("")).toList(), ',')),
                films.stream().map(v -> v.getId().toString()).toArray()
        );

        for (FilmFullGenre filmFullGenre : findFilmGenres) {
            Collection<Genre> genres = filmGenres.get(filmFullGenre.getFilmId());
            if (genres == null) {
                genres = new ArrayList<>();
                filmGenres.put(filmFullGenre.getFilmId(), genres);
            }

            genres.add(
                    Genre.builder()
                            .id(filmFullGenre.getGenreId())
                            .name(filmFullGenre.getGenreName())
                            .build()
            );
        }

        for (Film film : films) {
            Collection<Genre> genres = filmGenres.get(film.getId());
            if (genres != null) {
                film.setGenres(genres);
            }
        }
    }

    private void loadDirectors(Collection<Film> films) {
        if (films == null || films.isEmpty()) {
            return;
        }

        HashMap<Long, Collection<Director>> filmDirectors = new HashMap<>();

        Collection<FilmFullDirector> findFilmDirectors = filmDirectorBaseStorage.findMany(
                FIND_FILM_DIRECTORS.formatted(
                        Strings.join(Arrays.stream(new StringBuilder()
                                .repeat("?", films.size())
                                .toString()
                                .split("")).toList(), ',')
                ),
                films.stream().map(Film::getId).toArray()
        );

        for (FilmFullDirector findFilmDirector : findFilmDirectors) {
            Collection<Director> directors = filmDirectors.get(findFilmDirector.getFilmId());
            if (directors == null) {
                directors = new ArrayList<>();
                filmDirectors.put(findFilmDirector.getFilmId(), directors);
            }

            directors.add(
                    Director.builder()
                            .id(findFilmDirector.getDirectorId())
                            .name(findFilmDirector.getDirectorName())
                            .build()
            );
        }

        for (Film film : films) {
            Collection<Director> directors = filmDirectors.get(film.getId());
            if (directors != null) {
                film.setDirectors(directors);
            }
        }
    }

    public Collection<Film> getUserRecommendations(Long userId) {
        Optional<Long> similarUserOptional = filmLikesBaseStorage
                .findOne(FIND_SIMILAR_USER_QUERY, userId, userId)
                .map(FilmLikes::getUserId);

        if (similarUserOptional.isEmpty()) {
            return Collections.emptyList();
        }
        Long similarUserId = similarUserOptional.get();

        // Получаем список фильмов, которые понравились похожему пользователю, но отсутствуют у текущего пользователя
        Collection<Film> recommendedFilms = filmBaseStorage.findMany(FIND_RECOMMENDED_FILMS_QUERY, similarUserId, userId);

        return prepareFilmData(recommendedFilms);
    }
}
