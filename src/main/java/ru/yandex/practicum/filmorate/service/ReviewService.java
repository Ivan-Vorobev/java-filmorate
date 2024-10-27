package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.mappers.ReviewMapper;
import ru.yandex.practicum.filmorate.storage.review.ReviewRatingValue;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;

    public Review add(Review review) {
        assertNull(review, "Class review");

        Review createdReview = ReviewMapper.modelFromDto(
                reviewStorage.add(
                        ReviewMapper.dtoFromModel(review)
                )
        );

        return findReview(createdReview.getReviewId());
    }

    public Review update(Review review) {
        assertNull(review, "Class review");

        reviewStorage.update(
                ReviewMapper.dtoFromModel(review)
        );

        return findReview(review.getReviewId());
    }

    public void delete(Long reviewId) {
        assertNull(reviewId, "ReviewId");
        reviewStorage.delete(reviewId);
    }

    public Review findReview(Long reviewId) {
        assertNull(reviewId, "ReviewId");
        return ReviewMapper.modelFromDto(reviewStorage
                .findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Review not found. Id: " + reviewId)));
    }

    public Collection<Review> findFilmReviews(Long filmId, Integer count) {
        return reviewStorage.findByParams(filmId, count).stream()
                .map(ReviewMapper::modelFromDto)
                .toList();
    }

    public void addReviewRating(Long reviewId, Long userId, ReviewRatingValue ratingValue) {
        assertNull(reviewId, "ReviewId");
        assertNull(userId, "UserId");
        reviewStorage.changeRating(reviewId, userId, ratingValue);
    }

    public void deleteReviewRating(Long reviewId, Long userId) {
        assertNull(reviewId, "ReviewId");
        assertNull(userId, "UserId");
        reviewStorage.removeRating(reviewId, userId);
    }

    private void assertNull(Object value, String field) {
        if (value == null) {
            throw new IllegalArgumentException(field + " is null");
        }
    }
}
