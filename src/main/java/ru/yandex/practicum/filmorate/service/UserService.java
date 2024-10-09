package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserStorage userStorage;

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User findUser(Long userId) {
        User searchUser = userStorage.findById(userId);

        if (searchUser == null) {
            throw new NotFoundException("User not found. Id: " + userId);
        }

        return searchUser;
    }

    public User create(User user) {
        return userStorage.add(user);
    }

    public User update(User user) {
        findUser(user.getId());
        return userStorage.update(user);
    }

    public void addFriend(Long userId, Long friendId) {
        if (Objects.equals(userId, friendId)) {
            throw new IllegalArgumentException("Пользователь и друг совпадают");
        }

        User user = findUser(userId);
        User friend = findUser(friendId);

        userStorage.addFriend(user, friend);
    }


    public void deleteFriend(Long userId, Long friendId) {
        if (Objects.equals(userId, friendId)) {
            throw new IllegalArgumentException("Пользователь и друг совпадают");
        }

        User user = findUser(userId);
        User friend = findUser(friendId);

        userStorage.deleteFriend(user, friend);
    }

    public Collection<User> findFriends(Long userId) {
        User user = findUser(userId);

        Set<Long> friends = userStorage.getFriends(user);

        return friends.stream()
                .map(userStorage::findById)
                .collect(Collectors.toList());
    }

    public List<User> findCommonFriends(final Long userId, final Long otherId) {
        User user = findUser(userId);
        User otherUser = findUser(otherId);

        Set<Long> userFriends = userStorage.getFriends(user);
        Set<Long> otherUserFriends = userStorage.getFriends(otherUser);

        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .map(userStorage::findById)
                .collect(Collectors.toList());
    }
}
