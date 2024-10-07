package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.GenerateIdService;

import java.util.*;

@RequiredArgsConstructor
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, Set<Long>> friends = new HashMap<>();
    private final GenerateIdService idGenerator;

    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User findById(Long id) {
        User searchUser = users.get(id);

        if (searchUser == null) {
            throw new NotFoundException("User not found. Id: " + id);
        }

        return searchUser;
    }

    @Override
    public User addFriend(User user, User friend) {
        Set<Long> userFriends = this.getFriends(user);
        Set<Long> friendFriends = this.getFriends(friend);

        userFriends.add(friend.getId());
        friendFriends.add(user.getId());

        return user;
    }

    @Override
    public User deleteFriend(User user, User friend) {
        Set<Long> userFriends = this.getFriends(user);
        Set<Long> friendFriends = this.getFriends(friend);

        userFriends.remove(friend.getId());
        friendFriends.remove(user.getId());

        return user;
    }

    @Override
    public Set<Long> getFriends(User user) {
        if (user == null) {
            throw new NotFoundException("User is null");
        }

        Set<Long> userFriends = friends.get(user.getId());

        if (userFriends == null) {
            userFriends = new HashSet<>();
            friends.put(user.getId(), userFriends);
        }

        return userFriends;
    }

    @Override
    public User add(User user) {
        user.setId(idGenerator.generate());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        this.findById(user.getId());
        users.put(user.getId(), user);
        return user;
    }
}
