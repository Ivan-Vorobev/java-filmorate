package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.mappers.ReviewMapper;
import ru.yandex.practicum.filmorate.storage.dal.review.ReviewRatingValue;
import ru.yandex.practicum.filmorate.storage.dal.review.ReviewStorage;
import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final FilmService filmService;
    private final UserService userService;
    private final ReviewStorage reviewStorage;
    private final EventService eventService;

    public Review add(Review review) {
        assertNull(review, "Class review");

        filmService.findFilm(review.getFilmId());
        userService.findUser(review.getUserId());

        Review createdReview = ReviewMapper.modelFromDto(
                reviewStorage.add(
                        ReviewMapper.dtoFromModel(review)
                )
        );
        eventService.add(createdReview.getReviewId(), createdReview.getUserId(), EventType.REVIEW);
        return findReview(createdReview.getReviewId());
    }

    public Review update(Review review) {
        assertNull(review, "Class review");

        filmService.findFilm(review.getFilmId());
        userService.findUser(review.getUserId());

        reviewStorage.update(
                ReviewMapper.dtoFromModel(review)
        );
        Review updatedReview = findReview(review.getReviewId());
        eventService.update(updatedReview.getReviewId(), updatedReview.getUserId(), EventType.REVIEW);
        return updatedReview;
    }

    public void delete(Long reviewId) {
        assertNull(reviewId, "ReviewId");
        Review review = findReview(reviewId);
        reviewStorage.delete(reviewId);
        eventService.remove(review.getReviewId(), review.getUserId(), EventType.REVIEW);
    }

    public Review findReview(Long reviewId) {
        assertNull(reviewId, "ReviewId");
        return ReviewMapper.modelFromDto(reviewStorage
                .findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Review not found. Id: " + reviewId)));
    }

    public Collection<Review> findFilmReviews(Optional<Long> filmId, Integer count) {
        filmId.ifPresent(filmService::findFilm);
        return reviewStorage.findByParams(filmId.orElse(null), count).stream()
                .map(ReviewMapper::modelFromDto)
                .toList();
    }

    public void addReviewRating(Long reviewId, Long userId, ReviewRatingValue ratingValue) {
        userService.findUser(userId);
        assertNull(reviewId, "ReviewId");
        assertNull(userId, "UserId");
        reviewStorage.changeRating(reviewId, userId, ratingValue);
    }

    public void deleteReviewRating(Long reviewId, Long userId) {
        userService.findUser(userId);
        assertNull(reviewId, "ReviewId");
        assertNull(userId, "UserId");
    }

    private void assertNull(Object value, String field) {
        if (value == null) {
            throw new IllegalArgumentException(field + " is null");
        }
    }
}