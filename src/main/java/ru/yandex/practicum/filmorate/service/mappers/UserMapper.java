package ru.yandex.practicum.filmorate.service.mappers;

import ru.yandex.practicum.filmorate.storage.dal.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;

public class UserMapper {
    public static User modelFromDto(UserDto dto) {
        return User.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .login(dto.getLogin())
                .birthday(dto.getBirthday())
                .build();
    }

    public static UserDto dtoFromModel(User model) {
        return UserDto.builder()
                .id(model.getId())
                .name(model.getName())
                .email(model.getEmail())
                .login(model.getLogin())
                .birthday(model.getBirthday())
                .build();
    }
}
