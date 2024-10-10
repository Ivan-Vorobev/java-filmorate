package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Set;

public interface UserStorage {
    Collection<User> findAll();

    User add(User user);

    User update(User user);

    User findById(Long id);

    User addFriend(User user, User friend);

    User deleteFriend(User user, User friend);

    Set<Long> getFriends(User user);
}
