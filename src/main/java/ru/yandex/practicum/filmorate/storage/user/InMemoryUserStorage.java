package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.dal.dto.UserDto;
import ru.yandex.practicum.filmorate.storage.GenerateIdStorage;

import java.util.*;

@RequiredArgsConstructor
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, UserDto> users = new HashMap<>();
    private final Map<Long, Set<Long>> friends = new HashMap<>();
    private final GenerateIdStorage idGenerator;

    public Collection<UserDto> findAll() {
        return users.values();
    }

    @Override
    public Optional<UserDto> findById(Long id) {
        UserDto user = users.get(id);
        return user == null
                ? Optional.empty()
                : Optional.of(user);
    }

    @Override
    public void addFriend(UserDto user, UserDto friend) {
        Set<Long> userFriends = this.getFriends(user);

        userFriends.add(friend.getId());
    }

    @Override
    public void deleteFriend(UserDto user, UserDto friend) {
        Set<Long> userFriends = this.getFriends(user);
        Set<Long> friendFriends = this.getFriends(friend);

        userFriends.remove(friend.getId());
        friendFriends.remove(user.getId());
    }

    @Override
    public Set<Long> getFriends(UserDto user) {
        return friends.get(user.getId());
    }

    @Override
    public UserDto add(UserDto user) {
        user.setId(idGenerator.generate());
        users.put(user.getId(), user);
        friends.put(user.getId(), new HashSet<>());
        return user;
    }

    @Override
    public UserDto update(UserDto user) {
        users.put(user.getId(), user);
        return user;
    }
}
