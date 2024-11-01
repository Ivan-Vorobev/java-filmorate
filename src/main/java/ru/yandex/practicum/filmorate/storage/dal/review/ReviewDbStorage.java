package ru.yandex.practicum.filmorate.storage.dal.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.dal.BaseStorage;
import ru.yandex.practicum.filmorate.storage.dal.dto.ReviewDto;
import java.util.Collection;
import java.util.Optional;

@Repository
public class ReviewDbStorage implements ReviewStorage {
    private static final String DELETE_REVIEW_RATING_VALUE = """
            DELETE FROM review_ratings
            WHERE review_id = ?
                AND user_id = ?
            """;
    private static final String INSERT_REVIEW = """
            INSERT INTO reviews (film_id, user_id, is_positive, content)
            VALUES (?, ?, ?, ?)
            """;
    private static final String UPDATE_REVIEW = """
            UPDATE reviews SET
                is_positive = ?,
                content = ?
            WHERE id = ?
            """;
    private static final String INSERT_REVIEW_RATING_VALUE = """
            INSERT INTO review_ratings (review_id, user_id, "value")
            VALUES (?, ?, ?)
            """;
    private static final String FIND_REVIEW_QUERY = """
            SELECT
                r.*,
                SUM(rr."value") AS useful
            FROM reviews r
            LEFT JOIN review_ratings rr ON rr.review_id = r.id
            WHERE r.id = ?
            GROUP BY r.id
            """;
    private static final String FIND_ALL_FILM_REVIEWS_QUERY = """
            SELECT
                r.*,
                IFNULL(SUM(rr."value"), 0) AS useful
            FROM reviews r
            LEFT JOIN review_ratings rr ON rr.review_id = r.id
            ((WHERE_EXPRESSION))
            GROUP BY r.id
            ORDER BY useful DESC
            LIMIT ?
            """;
    private static final String DELETE_REVIEWS_QUERY = """
            DELETE FROM reviews
            WHERE id = ?
            """;
    private static final String DELETE_REVIEW_RATINGS_QUERY = """
            DELETE FROM review_ratings
            WHERE review_id = ?
            """;

    private final BaseStorage<ReviewDto> reviewDtoStorage;

    @Autowired
    public ReviewDbStorage(
            JdbcTemplate jdbc,
            RowMapper<ReviewDto> reviewDtoRowMapper
    ) {
        reviewDtoStorage = new BaseStorage<>(jdbc, reviewDtoRowMapper);
    }

    @Override
    public Optional<ReviewDto> findById(Long reviewId) {
        return reviewDtoStorage.findOne(FIND_REVIEW_QUERY, reviewId);
    }

    @Override
    public Collection<ReviewDto> findByParams(Long filmId, Integer limit) {
        limit = limit == null ? 10 : limit;
        if (filmId != null) {
            return reviewDtoStorage.findMany(
                    FIND_ALL_FILM_REVIEWS_QUERY.replace("((WHERE_EXPRESSION))", "WHERE r.film_id = ?"),
                    filmId,
                    limit
            );
        }
        return reviewDtoStorage.findMany(
                FIND_ALL_FILM_REVIEWS_QUERY.replace("((WHERE_EXPRESSION))", ""),
                limit
        );
    }

    @Override
    public void changeRating(Long reviewId, Long userId, ReviewRatingValue ratingValue) {
        removeRating(reviewId, userId);
        reviewDtoStorage.insert(INSERT_REVIEW_RATING_VALUE, reviewId, userId, ratingValue.value);
    }

    @Override
    public void removeRating(Long reviewId, Long userId) {
        reviewDtoStorage.delete(DELETE_REVIEW_RATING_VALUE, reviewId, userId);
    }

    @Override
    public ReviewDto add(ReviewDto reviewDto) {
        reviewDto.setId(
                reviewDtoStorage.insert(
                        INSERT_REVIEW,
                        reviewDto.getFilmId(),
                        reviewDto.getUserId(),
                        reviewDto.getIsPositive(),
                        reviewDto.getContent()
                )
        );

        return reviewDto;
    }

    @Override
    public void update(ReviewDto reviewDto) {
        reviewDtoStorage.update(
                UPDATE_REVIEW,
                reviewDto.getIsPositive(),
                reviewDto.getContent(),
                reviewDto.getId()
        );
    }

    @Override
    public void delete(Long reviewId) {
        reviewDtoStorage.delete(DELETE_REVIEWS_QUERY, reviewId);
        reviewDtoStorage.delete(DELETE_REVIEW_RATINGS_QUERY, reviewId);
    }
}
