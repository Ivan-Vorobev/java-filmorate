package ru.yandex.practicum.filmorate.service.mapper;

import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dal.model.Review;

import java.util.Collection;
import java.util.List;

public class ReviewDtoMapper {
    public static ReviewDto dtoFromModel(Review model) {
        return ReviewDto.builder()
                .reviewId(model.getId())
                .content(model.getContent())
                .isPositive(model.getIsPositive())
                .userId(model.getUserId())
                .filmId(model.getFilmId())
                .useful(model.getUseful() != null ? model.getUseful() : 0)
                .build();
    }

    public static Collection<ReviewDto> dtoFromModel(Collection<Review> models) {
        if (models == null) {
            return List.of();
        }

        return models.stream()
                .map(ReviewDtoMapper::dtoFromModel)
                .toList();
    }

    public static Review modelFromDto(ReviewDto dto) {
        return Review.builder()
                .id(dto.getReviewId())
                .filmId(dto.getFilmId())
                .userId(dto.getUserId())
                .isPositive(dto.getIsPositive())
                .content(dto.getContent())
                .useful(dto.getUseful())
                .build();
    }
}
