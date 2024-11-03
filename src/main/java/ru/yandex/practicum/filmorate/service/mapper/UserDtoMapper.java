package ru.yandex.practicum.filmorate.service.mapper;

import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.dal.model.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserDtoMapper {

    public static UserDto dtoFromModel(User model) {
        return UserDto.builder()
                .id(model.getId())
                .name(model.getName())
                .email(model.getEmail())
                .login(model.getLogin())
                .birthday(model.getBirthday())
                .build();
    }

    public static Collection<UserDto> dtoFromModel(Collection<User> models) {
        if (models == null) {
            return List.of();
        }

        return models.stream()
                .map(UserDtoMapper::dtoFromModel)
                .collect(Collectors.toList());
    }

    public static User modelFromDto(UserDto dto) {
        return User.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .login(dto.getLogin())
                .birthday(dto.getBirthday())
                .build();
    }
}