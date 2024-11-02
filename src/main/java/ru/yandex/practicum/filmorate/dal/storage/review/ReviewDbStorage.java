package ru.yandex.practicum.filmorate.dal.storage.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.storage.BaseStorage;
import ru.yandex.practicum.filmorate.dal.model.Review;

import java.util.Collection;
import java.util.Optional;

@Repository
@Primary
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

    private final BaseStorage<Review> reviewStorage;

    @Autowired
    public ReviewDbStorage(
            JdbcTemplate jdbc,
            RowMapper<Review> reviewRowMapper
    ) {
        reviewStorage = new BaseStorage<>(jdbc, reviewRowMapper);
    }

    @Override
    public Optional<Review> findById(Long reviewId) {
        return reviewStorage.findOne(FIND_REVIEW_QUERY, reviewId);
    }

    @Override
    public Collection<Review> findByParams(Long filmId, Integer limit) {
        limit = limit == null ? 10 : limit;
        if (filmId != null) {
            return reviewStorage.findMany(
                    FIND_ALL_FILM_REVIEWS_QUERY.replace("((WHERE_EXPRESSION))", "WHERE r.film_id = ?"),
                    filmId,
                    limit
            );
        }
        return reviewStorage.findMany(
                FIND_ALL_FILM_REVIEWS_QUERY.replace("((WHERE_EXPRESSION))", ""),
                limit
        );
    }

    @Override
    public void changeRating(Long reviewId, Long userId, ReviewRatingValue ratingValue) {
        removeRating(reviewId, userId);
        reviewStorage.insert(INSERT_REVIEW_RATING_VALUE, reviewId, userId, ratingValue.value);
    }

    @Override
    public void removeRating(Long reviewId, Long userId) {
        reviewStorage.delete(DELETE_REVIEW_RATING_VALUE, reviewId, userId);
    }

    @Override
    public Review add(Review review) {
        review.setId(
                reviewStorage.insert(
                        INSERT_REVIEW,
                        review.getFilmId(),
                        review.getUserId(),
                        review.getIsPositive(),
                        review.getContent()
                )
        );

        return review;
    }

    @Override
    public void update(Review review) {
        reviewStorage.update(
                UPDATE_REVIEW,
                review.getIsPositive(),
                review.getContent(),
                review.getId()
        );
    }

    @Override
    public void delete(Long reviewId) {
        reviewStorage.delete(DELETE_REVIEWS_QUERY, reviewId);
        reviewStorage.delete(DELETE_REVIEW_RATINGS_QUERY, reviewId);
    }
}
