package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.storage.dal.dto.UserDto;
import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    Collection<UserDto> findAll();

    UserDto add(UserDto user);

    UserDto update(UserDto user);

    void removeUserById(Long userId);

    Optional<UserDto> findById(Long id);

    void addFriend(UserDto user, UserDto friend);

    void deleteFriend(UserDto user, UserDto friend);

//    Set<Long> getFriends(UserDto user);

    Collection<UserDto> getFriends(Long userId);

    Collection<UserDto> getCommonFriendsOfUsers(Long userId, Long otherId);
}