package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.review.ReviewRatingValue;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/reviews")
@Slf4j
public class ReviewController {
    private final ReviewService reviewService;
    private final FilmService filmService;
    private final UserService userService;

    @PostMapping
    public Review addReview(
            @Valid @RequestBody Review review
    ) {
        filmService.findFilm(review.getFilmId());
        userService.findUser(review.getUserId());
        return reviewService.add(review);
    }

    @PutMapping
    public Review updateReview(
            @Valid @RequestBody Review review
    ) {
        filmService.findFilm(review.getFilmId());
        userService.findUser(review.getUserId());
        return reviewService.update(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(
            @PathVariable("id") Long reviewId
    ) {
        reviewService.delete(reviewId);
    }

    @GetMapping("/{id}")
    public Review getReview(
            @PathVariable("id") Long reviewId
    ) {
        return reviewService.findReview(reviewId);
    }

    @GetMapping
    public Collection<Review> getFilmReviews(
            @RequestParam("filmId") Optional<Long> filmId,
            @RequestParam("count") Optional<Integer> count
    ) {
        filmId.ifPresent(filmService::findFilm);
        Collection<Review> result = reviewService.findFilmReviews(filmId.orElse(null), count.orElse(null));
        return result != null ? result : List.of();
    }

    @PutMapping("/{id}/like/{userId}")
    public void reviewLike(
            @PathVariable("id") Long reviewId,
            @PathVariable("userId") Long userId
    ) {
        reviewService.findReview(reviewId);
        userService.findUser(userId);
        reviewService.addReviewRating(reviewId, userId, ReviewRatingValue.INCREASE);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void reviewDislike(
            @PathVariable("id") Long reviewId,
            @PathVariable("userId") Long userId
    ) {
        reviewService.findReview(reviewId);
        userService.findUser(userId);
        reviewService.addReviewRating(reviewId, userId, ReviewRatingValue.DECREASE);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteReviewLike(
            @PathVariable("id") Long reviewId,
            @PathVariable("userId") Long userId
    ) {
        reviewService.findReview(reviewId);
        userService.findUser(userId);
        reviewService.deleteReviewRating(reviewId, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteReviewDislike(
            @PathVariable("id") Long reviewId,
            @PathVariable("userId") Long userId
    ) {
        reviewService.findReview(reviewId);
        userService.findUser(userId);
        reviewService.deleteReviewRating(reviewId, userId);
    }
}
