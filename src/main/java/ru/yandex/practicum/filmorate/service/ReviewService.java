package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

    public ReviewDto add(ReviewDto reviewDto) {
        assertNull(reviewDto, "Class review");

        ReviewDto createdReviewDto = ReviewDtoMapper.modelFromDto(
                reviewStorage.add(
                        ReviewDtoMapper.dtoFromModel(reviewDto)
                )
        );
        eventService.add(createdReviewDto.getReviewId(), createdReviewDto.getUserId(), EventTypeDto.REVIEW);
        return findReview(createdReviewDto.getReviewId());
    }

    public ReviewDto update(ReviewDto reviewDto) {
        assertNull(reviewDto, "Class review");

        reviewStorage.update(
                ReviewDtoMapper.dtoFromModel(reviewDto)
        );
        ReviewDto updatedReviewDto = findReview(reviewDto.getReviewId());
        eventService.update(updatedReviewDto.getReviewId(), updatedReviewDto.getUserId(), EventTypeDto.REVIEW);
        return updatedReviewDto;
    }

    public void delete(Long reviewId) {
        assertNull(reviewId, "ReviewId");
        ReviewDto reviewDto = findReview(reviewId);
        reviewStorage.delete(reviewId);
        eventService.remove(reviewDto.getReviewId(), reviewDto.getUserId(), EventTypeDto.REVIEW);
    }

    public ReviewDto findReview(Long reviewId) {
        assertNull(reviewId, "ReviewId");
        return ReviewDtoMapper.modelFromDto(reviewStorage
                .findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Review not found. Id: " + reviewId)));
    }

    public Collection<ReviewDto> findFilmReviews(Long filmId, Integer count) {
        return reviewStorage.findByParams(filmId, count).stream()
                .map(ReviewDtoMapper::modelFromDto)
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
    }

    private void assertNull(Object value, String field) {
        if (value == null) {
            throw new IllegalArgumentException(field + " is null");
        }
    }
}