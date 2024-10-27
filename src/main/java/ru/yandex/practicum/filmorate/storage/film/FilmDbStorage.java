package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.dal.BaseStorage;
import ru.yandex.practicum.filmorate.storage.dal.dto.FilmDto;
import ru.yandex.practicum.filmorate.storage.dal.dto.FilmLikesDto;
import ru.yandex.practicum.filmorate.storage.dal.dto.GenreDto;
import ru.yandex.practicum.filmorate.storage.dal.dto.UserDto;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
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
    private final BaseStorage<UserDto> userDtoBaseStorage;


    @Autowired
    public FilmDbStorage(
            JdbcTemplate jdbc,
            RowMapper<FilmDto> filmDtoRowMapper,
            RowMapper<FilmLikesDto> filmLikesRowMapper,
            RowMapper<UserDto> userDtoRowMapper
    ) {
        filmBaseStorage = new BaseStorage<>(jdbc, filmDtoRowMapper);
        filmLikesBaseStorage = new BaseStorage<>(jdbc, filmLikesRowMapper);
        userDtoBaseStorage = new BaseStorage<>(jdbc, userDtoRowMapper);
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


//    public Collection<FilmDto> getUserRecommendations(Long userId) {
//        // SQL-запрос для поиска ID пользователя с наибольшим количеством совпадений по лайкам
//        String findSimilarUserQuery = "SELECT l1.user_id " +
//                "FROM film_likes l1 " +
//                "JOIN film_likes l2 ON l1.film_id = l2.film_id " +
//                "GROUP BY l1.user_id " +
//                "ORDER BY COUNT(l1.film_id) DESC " +
//                "LIMIT 1";
//
//        // Получаем ID пользователя, который имеет наибольшее количество совпадений по лайкам с текущим пользователем
//        Optional<Long> similarUserOptional = filmLikesBaseStorage
//                .findOne(findSimilarUserQuery, userId, userId)
//                .map(FilmLikesDto::getUserId);
//
//        if (similarUserOptional.isEmpty()) {
//            return Collections.emptyList();
//        }
//
//        Long similarUserId = similarUserOptional.get();
//
//        // SQL-запрос для получения фильмов, которые понравились похожему пользователю, но не текущему
//        String recommendedFilmsQuery = "SELECT f.id, f.name, f.description, f.release_date, " +
//                "f.duration, r.id AS rating_id, r.name AS rating_name " +
//                "FROM films f " +
//                "JOIN film_likes l ON f.id = l.film_id " +
//                "LEFT JOIN rating r ON f.rating_id = r.id " +
//                "WHERE l.user_id = ? AND f.id NOT IN (" +
//                "   SELECT film_id FROM film_likes WHERE user_id = ?" +
//                ")";
//
//        // Получаем список фильмов, которые понравились похожему пользователю, но отсутствуют у текущего пользователя
//        Collection<FilmDto> recommendedFilms = filmBaseStorage.findMany(recommendedFilmsQuery, similarUserId, userId);
//
//        return prepareFilmDtoData(recommendedFilms);
//    }

    public Collection<FilmDto> getUserRecommendations(Long userId) {
        // Получаем всех пользователей и их лайки
        Collection<FilmLikesDto> allLikes = filmLikesBaseStorage.findMany("SELECT * FROM film_likes");
        log.info("размер allLikes " + allLikes.size());
        // Группируем лайки по пользователям
        Map<Long, Set<Long>> userLikes = new HashMap<>();
        for (FilmLikesDto like : allLikes) {
            userLikes.computeIfAbsent(like.getUserId(), k -> new HashSet<>()).add(like.getFilmId());
        }

        // Получаем лайки текущего пользователя
        Set<Long> currentUserLikes = userLikes.getOrDefault(userId, Collections.emptySet());
        log.info("размер currentUserLikes " + currentUserLikes.size());

        // Находим пользователей с максимальным количеством пересечений
        Map<Long, Integer> similarUsers = new HashMap<>();
        for (Map.Entry<Long, Set<Long>> entry : userLikes.entrySet()) {
            Long otherUserId = entry.getKey();
            if (!otherUserId.equals(userId)) {
                Set<Long> likes = entry.getValue();
                // Находим пересечения лайков
                int intersectionCount = (int) likes.stream().filter(currentUserLikes::contains).count();
                if (intersectionCount > 0) {
                    similarUsers.put(otherUserId, intersectionCount);
                }
            }
        }

        // Находим пользователя с максимальным количеством пересечений
        Long mostSimilarUserId = similarUsers.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
        log.info("размер mostSimilarUserId " + mostSimilarUserId);

        if (mostSimilarUserId == null) {
            return Collections.emptyList();
        }

        // Получаем фильмы, которые пролайкал похожий пользователь
        Set<Long> similarUserLikes = userLikes.get(mostSimilarUserId);
        log.info("размер similarUserLikes " + similarUserLikes.size());
        // Исключаем фильмы, которые лайкнул текущий пользователь
        similarUserLikes.removeAll(currentUserLikes);
        log.info("ошибка до removeAll");

        /// TODO ошибка в запросе либо DTO мапере
        // Получаем детали фильмов, которые можно рекомендовать
        Collection<FilmDto> recommendedFilms = filmBaseStorage.findMany(
                "SELECT f.id, f.name, f.description, f.release_date, f.duration, r.id AS rating_id, r.name AS rating_name " +
                        "FROM films f " +
                        "LEFT JOIN rating r ON f.rating_id = r.id " +
                        "WHERE f.id IN (" +
                        String.join(",", similarUserLikes.stream().map(String::valueOf).toArray(String[]::new)) + ")"
        );


        return recommendedFilms;
    }

}


//SELECT l1.user_id
//FROM film_likes l1
//JOIN film_likes l2 ON l1.film_id = l2.film_id
//WHERE l2.user_id = 30 AND l1.user_id <> 30
//GROUP BY l1.user_id
//ORDER BY COUNT(l1.film_id) DESC
//LIMIT 1;