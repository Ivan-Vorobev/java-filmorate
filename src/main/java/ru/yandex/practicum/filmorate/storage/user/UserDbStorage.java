package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.dal.BaseStorage;
import ru.yandex.practicum.filmorate.storage.dal.dto.UserDto;
import ru.yandex.practicum.filmorate.storage.dal.dto.UserFriendsDto;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class UserDbStorage implements UserStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String INSERT_USER_QUERY = "INSERT INTO users (email, login, name, birthday)" +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER_QUERY = """
            UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?
            """;
    private static final String INSERT_FRIEND_QUERY = "INSERT INTO user_friends (user_id, friend_id) VALUES (?, ?)";
    private static final String DELETE_USER_FRIEND_QUERY = "DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?";
    private static final String FIND_USER_LIKES_QUERY = "SELECT * FROM user_friends WHERE user_id = ?";

    private final BaseStorage<UserDto> userBaseStorage;
    private final BaseStorage<UserFriendsDto> userFriendBaseStorage;

    public UserDbStorage(
            JdbcTemplate jdbc,
            @Qualifier("userDtoMapper") RowMapper<UserDto> userDtoRowMapper,
            @Qualifier("userFriendsDtoMapper") RowMapper<UserFriendsDto> userFriendRowMapper
    ) {
        userBaseStorage = new BaseStorage<>(jdbc, userDtoRowMapper);
        userFriendBaseStorage = new BaseStorage<>(jdbc, userFriendRowMapper);
    }

    @Override
    public Collection<UserDto> findAll() {
        return userBaseStorage.findMany(FIND_ALL_QUERY);
    }

    @Override
    public void addFriend(UserDto user, UserDto friend) {
        try {
            userFriendBaseStorage.insert(INSERT_FRIEND_QUERY, user.getId(), friend.getId());
        } catch (Exception ignored) {
        }
    }

    @Override
    public UserDto add(UserDto user) {
        Long id = userBaseStorage.insert(
                INSERT_USER_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    @Override
    public UserDto update(UserDto user) {
        userBaseStorage.update(
                UPDATE_USER_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return user;
    }

    @Override
    public Optional<UserDto> findById(Long id) {
        return userBaseStorage.findOne(FIND_BY_ID_QUERY, id);
    }

    @Override
    public void deleteFriend(UserDto user, UserDto friend) {
        userFriendBaseStorage.delete(DELETE_USER_FRIEND_QUERY, user.getId(), friend.getId());
    }

    @Override
    public Set<Long> getFriends(UserDto user) {
        return userFriendBaseStorage.findMany(FIND_USER_LIKES_QUERY, user.getId()).stream()
                .map(UserFriendsDto::getFriendId)
                .collect(Collectors.toSet());
    }
}
