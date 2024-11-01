package ru.yandex.practicum.filmorate.storage.dal.review;

import ru.yandex.practicum.filmorate.storage.dal.dto.ReviewDto;

import java.util.Collection;
import java.util.Optional;

public interface ReviewStorage {
    Optional<ReviewDto> findById(Long reviewId);

    Collection<ReviewDto> findByParams(Long filmId, Integer limit);

    void changeRating(Long reviewId, Long userId, ReviewRatingValue ratingValue);

    void removeRating(Long reviewId, Long userId);

    ReviewDto add(ReviewDto reviewDto);

    void update(ReviewDto reviewDto);

    void delete(Long reviewId);
}
