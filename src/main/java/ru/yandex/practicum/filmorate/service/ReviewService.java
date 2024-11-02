package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.dto.EventTypeDto;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.service.mapper.ReviewDtoMapper;
import ru.yandex.practicum.filmorate.dal.storage.review.ReviewRatingValue;
import ru.yandex.practicum.filmorate.dal.storage.review.ReviewStorage;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final EventService eventService;
    private final FilmService filmService;
    private final UserService userService;

    public ReviewDto create(ReviewDto reviewDto) {
        filmService.findFilmById(reviewDto.getFilmId());
        userService.getUserById(reviewDto.getUserId());

        ReviewDto createdReviewDto = ReviewDtoMapper.dtoFromModel(
                reviewStorage.add(
                        ReviewDtoMapper.modelFromDto(reviewDto)
                )
        );
        eventService.add(createdReviewDto.getReviewId(), createdReviewDto.getUserId(), EventTypeDto.REVIEW);
        return findReviewById(createdReviewDto.getReviewId());
    }

    public ReviewDto update(ReviewDto reviewDto) {
        filmService.findFilmById(reviewDto.getFilmId());
        userService.getUserById(reviewDto.getUserId());

        reviewStorage.update(
                ReviewDtoMapper.modelFromDto(reviewDto)
        );
        ReviewDto updatedReviewDto = findReviewById(reviewDto.getReviewId());
        eventService.update(updatedReviewDto.getReviewId(), updatedReviewDto.getUserId(), EventTypeDto.REVIEW);
        return updatedReviewDto;
    }

    public void delete(Long reviewId) {
        if (reviewId == null) {
            return;
        }

        ReviewDto reviewDto = findReviewById(reviewId);
        reviewStorage.delete(reviewId);
        eventService.remove(reviewDto.getReviewId(), reviewDto.getUserId(), EventTypeDto.REVIEW);
    }

    public ReviewDto findReviewById(Long reviewId) {
        return ReviewDtoMapper.dtoFromModel(reviewStorage
                .findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Review not found. Id: " + reviewId)));
    }

    public Collection<ReviewDto> findUserFilmReviews(Long filmId, Integer count) {
        FilmDto filmDto = filmService.findFilmById(filmId);
        return ReviewDtoMapper.dtoFromModel(reviewStorage.findByParams(filmDto.getId(), count));
    }

    public Collection<ReviewDto> findFilmReviews(Integer count) {
        return ReviewDtoMapper.dtoFromModel(reviewStorage.findByParams(null, count));
    }

    public void addReviewRating(Long reviewId, Long userId, ReviewRatingValue ratingValue) {
        findReviewById(reviewId);
        userService.getUserById(userId);
        reviewStorage.changeRating(reviewId, userId, ratingValue);
    }

    public void deleteReviewRating(Long reviewId, Long userId) {
        findReviewById(reviewId);
        userService.getUserById(userId);
        reviewStorage.removeRating(reviewId, userId);
    }
}