package ru.yandex.practicum.filmorate.dal.storage.user;

import ru.yandex.practicum.filmorate.dal.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    Collection<User> findAll();

    User add(User user);

    User update(User user);

    void removeUserById(Long userId);

    Optional<User> findById(Long id);

    void addFriend(User user, User friend);

    void deleteFriend(User user, User friend);

    Collection<User> getFriends(Long userId);

    Collection<User> getCommonFriendsOfUsers(Long userId, Long otherId);
}