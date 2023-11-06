package ru.yandex.practicum.filmorate.storage.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.exceptions.AddExistObjectException;
import ru.yandex.practicum.filmorate.exception.exceptions.UpdateNonExistObjectException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class InMemoryUserStorageTest {
    public UserStorage userStorage;

    @BeforeEach
    public void setUserStorage() {
        this.userStorage = new InMemoryUserStorage();
    }

    public User createUser(String email, String login, String name, int year, int month, int day) {
        User user = new User();
        user.setEmail(email);
        user.setLogin(login);
        user.setName(name);
        user.setBirthday(LocalDate.of(year, month, day));
        return user;
    }

    public User createUser(int id, String email, String login, String name, int year, int month, int day) {
        User user = createUser(email, login, name, year, month, day);
        user.setId(id);
        return user;
    }

    public User createUser(int id, String email, String login, String name, int year, int month, int day,
                           Set<Integer> friends) {
        User user = createUser(id, email, login, name, year, month, day);
        user.setFriends(friends);
        return user;
    }

    public void fillUsers() {
        for (int i = 1; i <= 20; ++i) {
            userStorage.addUser(createUser(i + "user@e.mail", i + "login", i + "name", 2000, 1, i));
        }
    }

    @Test
    public void getAllUsersEmpty() {
        Assertions.assertEquals(new HashMap<>(), userStorage.getAllUsers(),
                "Возвращается непустой список пользователей");
    }

    @Test
    public void getAllUsersFill() {
        fillUsers();

        Assertions.assertEquals(20, userStorage.getAllUsers().size(),
                "Возвращается неправильное количество пользователей");
    }

    @Test
    public void getExistUser() {
        userStorage.addUser(createUser(1 + "user@e.mail", 1 + "login", 1 + "name",
                2000, 1, 1));

        Assertions.assertEquals(createUser(1, 1 + "user@e.mail", 1 + "login", 1 + "name",
                        2000, 1, 1, new HashSet<>()),
                userStorage.getUser(1),
                "Пользователь с указанным id невозможно получить");
    }

    @Test
    public void getNonExistUser() {
        Assertions.assertThrows(UpdateNonExistObjectException.class,
                () -> userStorage.getUser(1),
                "при добавлении пользователя ожидалось UpdateNonExistObjectException");
    }


    @Test
    public void addFirstUser() {
        userStorage.addUser(createUser(1 + "user@e.mail", 1 + "login", 1 + "name",
                2000, 1, 1));

        Assertions.assertEquals(createUser(1, 1 + "user@e.mail", 1 + "login", 1 + "name",
                        2000, 1, 1, new HashSet<>()),
                userStorage.getUser(1),
                "Первый созданный пользователь неправильно добавляется");
    }

    @Test
    public void addNewUser() {
        fillUsers();

        userStorage.addUser(createUser(21 + "user@e.mail", 21 + "login", 21 + "name",
                2000, 1, 21));

        Assertions.assertEquals(createUser(21, 21 + "user@e.mail", 21 + "login", 21 + "name",
                        2000, 1, 21, new HashSet<>()),
                userStorage.getUser(21),
                "Новый пользователь неправильно добавляется");
    }

    @Test
    public void addExistUser() {
        fillUsers();

        Assertions.assertThrows(AddExistObjectException.class,
                () -> userStorage.addUser(createUser(1, 1 + "user@e.mail", 1 + "login", 1 + "name",
                        2000, 1, 1)),
                "При добавлнении пользователя ожидалось AddExistObjectException");
    }

    @Test
    public void addUserWithoutOptionalParameters() {
        userStorage.addUser(createUser(1 + "user@e.mail", 1 + "login", null,
                2000, 1, 1));

        Assertions.assertEquals(createUser(1, 1 + "user@e.mail", 1 + "login", 1 + "login",
                        2000, 1, 1, new HashSet<>()),
                userStorage.getUser(1),
                "Необазятельные параметры пользователя неправильно обрабатываются");
    }


    @Test
    public void updateExistUser() {
        userStorage.addUser(createUser(1 + "user@e.mail", 1 + "login", 1 + "name",
                2000, 1, 1));

        Assertions.assertEquals(createUser(1, 2 + "user@e.mail", 2 + "login", 2 + "name",
                        2000, 1, 2, new HashSet<>()),
                userStorage.updateUser(createUser(1, 2 + "user@e.mail", 2 + "login", 2 + "name",
                        2000, 1, 2)),
                "Пользователь неправильно обновляется");
    }

    @Test
    public void updateNonExistUser() {
        Assertions.assertThrows(UpdateNonExistObjectException.class,
                () -> userStorage.updateUser(createUser(1, 1 + "user@e.mail", 1 + "login",
                        1 + "name", 2000, 1, 1)),
                "При обновлении пользователя ожидалось UpdateNonExistObjectException");
    }

    @Test
    public void deleteExistUser() {
        userStorage.addUser(createUser(1, 1 + "user@e.mail", 1 + "login", 1 + "name",
                2000, 1, 1));

        Assertions.assertEquals(createUser(1, 1 + "user@e.mail", 1 + "login", 1 + "name",
                        2000, 1, 1, new HashSet<>()),
                userStorage.deleteUser(1),
                "Пользователь неправильно удаляется");
    }

    @Test
    public void deleteNonExistUser() {
        Assertions.assertThrows(UpdateNonExistObjectException.class,
                () -> userStorage.deleteUser(1),
                "При удалении пользователя ожидалось UpdateNonExistObjectException");
    }
}
