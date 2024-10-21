package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.dal.dto.UserDto;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.mappers.UserMapper;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        return userStorage.findAll().stream()
                .map(UserMapper::modelFromDto)
                .collect(Collectors.toList());
    }

    public User findUser(Long userId) {
        UserDto userDto = userStorage
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found. Id: " + userId));

        return UserMapper.modelFromDto(userDto);
    }

    public User create(User user) {
        return UserMapper.modelFromDto(
                userStorage.add(
                        UserMapper.dtoFromModel(user)
                )
        );
    }

    public User update(User user) {
        findUser(user.getId());
        return UserMapper.modelFromDto(
                userStorage.update(
                        UserMapper.dtoFromModel(user)
                )
        );
    }

    public void addFriend(Long userId, Long friendId) {
        if (Objects.equals(userId, friendId)) {
            throw new IllegalArgumentException("Пользователь и друг совпадают");
        }

        User user = findUser(userId);
        User friend = findUser(friendId);

        userStorage.addFriend(UserMapper.dtoFromModel(user), UserMapper.dtoFromModel(friend));
    }


    public void deleteFriend(Long userId, Long friendId) {
        if (Objects.equals(userId, friendId)) {
            throw new IllegalArgumentException("Пользователь и друг совпадают");
        }

        User user = findUser(userId);
        User friend = findUser(friendId);

        userStorage.deleteFriend(UserMapper.dtoFromModel(user), UserMapper.dtoFromModel(friend));
    }

    public Collection<User> findFriends(Long userId) {
        User user = findUser(userId);

        Set<Long> friends = userStorage.getFriends(UserMapper.dtoFromModel(user));

        return friends.stream()
                .map(this::findUser)
                .collect(Collectors.toList());
    }

    public List<User> findCommonFriends(final Long userId, final Long otherId) {
        User user = findUser(userId);
        User otherUser = findUser(otherId);

        Set<Long> userFriends = userStorage.getFriends(UserMapper.dtoFromModel(user));
        Set<Long> otherUserFriends = userStorage.getFriends(UserMapper.dtoFromModel(otherUser));

        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .map(this::findUser)
                .collect(Collectors.toList());
    }
}
