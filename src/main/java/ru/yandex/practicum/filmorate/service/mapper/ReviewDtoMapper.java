package ru.yandex.practicum.filmorate.service.mapper;

import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dal.model.Review;

public class ReviewDtoMapper {
    public static ReviewDto modelFromDto(Review dto) {
        return ReviewDto.builder()
                .reviewId(dto.getId())
                .content(dto.getContent())
                .isPositive(dto.getIsPositive())
                .userId(dto.getUserId())
                .filmId(dto.getFilmId())
                .useful(dto.getUseful() != null ? dto.getUseful() : 0)
                .build();
    }

    public static Review dtoFromModel(ReviewDto model) {
        return Review.builder()
                .id(model.getReviewId())
                .filmId(model.getFilmId())
                .userId(model.getUserId())
                .isPositive(model.getIsPositive())
                .content(model.getContent())
                .useful(model.getUseful())
                .build();
    }
}
