package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.dal.storage.review.ReviewRatingValue;

import java.util.Collection;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/reviews")
@Slf4j
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ReviewDto create(
            @Valid @RequestBody ReviewDto reviewDto
    ) {
        return reviewService.create(reviewDto);
    }

    @PutMapping
    public ReviewDto update(
            @Valid @RequestBody ReviewDto reviewDto
    ) {
        return reviewService.update(reviewDto);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable("id") Long reviewId
    ) {
        reviewService.delete(reviewId);
    }

    @GetMapping("/{id}")
    public ReviewDto findReviewById(
            @PathVariable("id") Long reviewId
    ) {
        return reviewService.findReviewById(reviewId);
    }

    @GetMapping
    public Collection<ReviewDto> getFilteredFilmReviews(
            @RequestParam(value = "filmId", required = false) Long filmId,
            @RequestParam(value = "count", required = false) Integer count
    ) {
        if (filmId == null) {
            return reviewService.findFilmReviews(count);
        }

        return reviewService.findUserFilmReviews(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void reviewLike(
            @PathVariable("id") Long reviewId,
            @PathVariable("userId") Long userId
    ) {
        reviewService.addReviewRating(reviewId, userId, ReviewRatingValue.INCREASE);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void reviewDislike(
            @PathVariable("id") Long reviewId,
            @PathVariable("userId") Long userId
    ) {
        reviewService.addReviewRating(reviewId, userId, ReviewRatingValue.DECREASE);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteReviewLike(
            @PathVariable("id") Long reviewId,
            @PathVariable("userId") Long userId
    ) {
        reviewService.deleteReviewRating(reviewId, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteReviewDislike(
            @PathVariable("id") Long reviewId,
            @PathVariable("userId") Long userId
    ) {
        reviewService.deleteReviewRating(reviewId, userId);
    }
}