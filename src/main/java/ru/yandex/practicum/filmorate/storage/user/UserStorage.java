package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {
    Map<Integer, User> getAllUsers();

    User getUser(int userId);

    User addUser(User user);

    User updateUser(User user);

    User deleteUser(int userId);

    Map<Integer, List<Integer>> getUsersLikes(int userId);
}