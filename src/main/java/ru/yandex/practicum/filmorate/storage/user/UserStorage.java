package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.storage.dal.dto.UserDto;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface UserStorage {
    Collection<UserDto> findAll();

    UserDto add(UserDto user);

    UserDto update(UserDto user);

    Optional<UserDto> findById(Long id);

    void addFriend(UserDto user, UserDto friend);

    void deleteFriend(UserDto user, UserDto friend);

    Set<Long> getFriends(UserDto user);
}
