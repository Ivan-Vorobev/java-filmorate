package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.EventTypeDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.dal.model.User;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.service.mapper.UserDtoMapper;
import ru.yandex.practicum.filmorate.dal.storage.user.UserStorage;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;
    private final EventService eventService;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, EventService eventService) {
        this.userStorage = userStorage;
        this.eventService = eventService;
    }

    public Collection<UserDto> findAll() {
        return userStorage.findAll().stream()
                .map(UserDtoMapper::modelFromDto)
                .collect(Collectors.toList());
    }

    public UserDto findUser(Long userId) {
        User user = userStorage
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found. Id: " + userId));

        return UserDtoMapper.modelFromDto(user);
    }

    public UserDto create(UserDto userDto) {
        return UserDtoMapper.modelFromDto(
                userStorage.add(
                        UserDtoMapper.dtoFromModel(userDto)
                )
        );
    }

    public UserDto update(UserDto userDto) {
        findUser(userDto.getId());
        return UserDtoMapper.modelFromDto(
                userStorage.update(
                        UserDtoMapper.dtoFromModel(userDto)
                )
        );
    }

    public void removeUserById(Long userId) {
        UserDto userDto = findUser(userId);
        userStorage.removeUserById(userId);
    }

    public void addFriend(Long userId, Long friendId) {
        if (Objects.equals(userId, friendId)) {
            throw new IllegalArgumentException("Пользователь и друг совпадают");
        }
        UserDto userDto = findUser(userId);
        UserDto friend = findUser(friendId);
        userStorage.addFriend(UserDtoMapper.dtoFromModel(userDto), UserDtoMapper.dtoFromModel(friend));
        eventService.add(friendId, userId, EventTypeDto.FRIEND);
    }

    public void deleteFriend(Long userId, Long friendId) {
        if (Objects.equals(userId, friendId)) {
            throw new IllegalArgumentException("Пользователь и друг совпадают");
        }
        UserDto userDto = findUser(userId);
        UserDto friend = findUser(friendId);
        userStorage.deleteFriend(UserDtoMapper.dtoFromModel(userDto), UserDtoMapper.dtoFromModel(friend));
        eventService.remove(friendId, userId, EventTypeDto.FRIEND);
    }

    public Collection<UserDto> findFriends(Long userId) {
        UserDto userDto = findUser(userId);
        Collection<User> friends = userStorage.getFriends(userId);
        return friends.stream()
                .map(UserDtoMapper::modelFromDto)
                .collect(Collectors.toList());
    }

    public List<UserDto> findCommonFriends(final Long userId, final Long otherId) {
        UserDto userDto = findUser(userId);
        UserDto otherUserDto = findUser(otherId);
        Collection<User> commonFriendsOfUsers = userStorage.getCommonFriendsOfUsers(userDto.getId(), otherUserDto.getId());
        return commonFriendsOfUsers.stream()
                .map(UserDtoMapper::modelFromDto)
                .toList();
    }
}