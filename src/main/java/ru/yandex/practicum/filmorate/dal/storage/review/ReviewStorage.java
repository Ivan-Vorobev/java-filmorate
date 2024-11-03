package ru.yandex.practicum.filmorate.dal.storage.review;

import ru.yandex.practicum.filmorate.dal.model.Review;
import java.util.Collection;
import java.util.Optional;

public interface ReviewStorage {
    Optional<Review> findById(Long reviewId);

    Collection<Review> findByParams(Long filmId, Integer limit);

    void changeRating(Long reviewId, Long userId, ReviewRatingValue ratingValue);

    void deleteRating(Long reviewId, Long userId);

    Review add(Review review);

    void update(Review review);

    void delete(Long reviewId);

    void deleteReviewByUserId(Long userId);

    void deleteReviewByFilmId(Long filmId);

    void deleteReviewRatingsByUserId(Long userId);
}