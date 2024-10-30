package ru.yandex.practicum.filmorate.service.mappers;

import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.dal.dto.ReviewDto;

public class ReviewMapper {
    public static Review modelFromDto(ReviewDto dto) {
        return Review.builder()
                .reviewId(dto.getId())
                .content(dto.getContent())
                .isPositive(dto.getIsPositive())
                .userId(dto.getUserId())
                .filmId(dto.getFilmId())
                .useful(dto.getUseful() != null ? dto.getUseful() : 0)
                .build();
    }

    public static ReviewDto dtoFromModel(Review model) {
        return ReviewDto.builder()
                .id(model.getReviewId())
                .filmId(model.getFilmId())
                .userId(model.getUserId())
                .isPositive(model.getIsPositive())
                .content(model.getContent())
                .useful(model.getUseful())
                .build();
    }
}
