package ru.yandex.practicum.filmorate.service.mapper;

import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.dal.model.User;

public class UserDtoMapper {

    public static UserDto modelFromDto(User dto) {
        return UserDto.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .login(dto.getLogin())
                .birthday(dto.getBirthday())
                .build();
    }

    public static User dtoFromModel(UserDto model) {
        return User.builder()
                .id(model.getId())
                .name(model.getName())
                .email(model.getEmail())
                .login(model.getLogin())
                .birthday(model.getBirthday())
                .build();
    }
}