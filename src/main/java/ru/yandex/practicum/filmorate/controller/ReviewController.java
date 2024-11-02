package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.dal.storage.review.ReviewRatingValue;

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
    public ReviewDto addReview(
            @Valid @RequestBody ReviewDto reviewDto
    ) {
        filmService.findFilm(reviewDto.getFilmId());
        userService.findUser(reviewDto.getUserId());
        return reviewService.add(reviewDto);
    }

    @PutMapping
    public ReviewDto updateReview(
            @Valid @RequestBody ReviewDto reviewDto
    ) {
        filmService.findFilm(reviewDto.getFilmId());
        userService.findUser(reviewDto.getUserId());
        return reviewService.update(reviewDto);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(
            @PathVariable("id") Long reviewId
    ) {
        reviewService.delete(reviewId);
    }

    @GetMapping("/{id}")
    public ReviewDto getReview(
            @PathVariable("id") Long reviewId
    ) {
        return reviewService.findReview(reviewId);
    }

    @GetMapping
    public Collection<ReviewDto> getFilmReviews(
            @RequestParam("filmId") Optional<Long> filmId,
            @RequestParam("count") Optional<Integer> count
    ) {
        filmId.ifPresent(filmService::findFilm);
        Collection<ReviewDto> result = reviewService.findFilmReviews(filmId.orElse(null), count.orElse(null));
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