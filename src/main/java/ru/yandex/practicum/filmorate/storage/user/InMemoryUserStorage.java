package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.exceptions.AddExistObjectException;
import ru.yandex.practicum.filmorate.exception.exceptions.UpdateNonExistObjectException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users;
    private int lastId;

    public InMemoryUserStorage() {
        users = new HashMap<>();
        lastId = 1;
    }

    @Override
    public Map<Integer, User> getAllUsers() {
        return users;
    }

    @Override
    public User getUser(int userId) {
        checkContainsUser(userId);
        return users.get(userId);
    }

    @Override
    public User addUser(User user) {
        checkNonContainsUser(user.getId());
        user.setId(lastId++);
        users.put(user.getId(), fillingOptionalParameters(user));
        return user;
    }

    @Override
    public User updateUser(User user) {
        checkContainsUser(user.getId());
        users.put(user.getId(), fillingOptionalParameters(user));
        return user;
    }

    @Override
    public User deleteUser(int userId) {
        User user = getUser(userId);
        users.remove(userId);
        return user;
    }

    @Override
    public Map<Integer, List<Integer>> getUsersLikes(int userId) {
        return null;
    }

    private User fillingOptionalParameters(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        return user;
    }

    private void checkNonContainsUser(int userId) {
        if (users.containsKey(userId)) {
            throw new AddExistObjectException("User с указанным id = " + userId + " уже существует");
        }
    }

    private void checkContainsUser(int userId) {
        if (!users.containsKey(userId)) {
            throw new UpdateNonExistObjectException("User с указанным id = " + userId + " не существует");
        }
    }

    @Override
    public Map<Integer, List<Integer>> getUsersLikes(int userId) {
        return null;
    }
}