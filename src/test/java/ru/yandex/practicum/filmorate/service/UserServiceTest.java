package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.exceptions.AddExistObjectException;
import ru.yandex.practicum.filmorate.exception.exceptions.IncorrectObjectModificationException;
import ru.yandex.practicum.filmorate.exception.exceptions.UpdateNonExistObjectException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class UserServiceTest {
    public UserService userService;

    @BeforeEach
    public void setUserService() {
        userService = new UserService(new InMemoryUserStorage());
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
            userService.addUser(createUser(i + "user@e.mail", i + "login", i + "name", 2000, 1, i));
        }
    }

    @Test
    public void getAllUsersEmpty() {
        Assertions.assertEquals(new ArrayList<>(), userService.getAllUsers(),
                "Неправильно возвращается пустой список пользователей");
    }

    @Test
    public void getAllUsersFill() {
        fillUsers();

        Assertions.assertEquals(20, userService.getAllUsers().size(),
                "Неправильно возвращается заполненный список пользователей");
    }

    @Test
    public void getExistUser() {
        userService.addUser(createUser(1 + "user@e.mail", 1 + "login", 1 + "name",
                2000, 1, 1));

        Assertions.assertEquals(createUser(1, 1 + "user@e.mail", 1 + "login", 1 + "name",
                        2000, 1, 1, new HashSet<>()),
                userService.getUser(1),
                "Пользователь с указанным id невозможно получить");
    }

    @Test
    public void getNonExistUser() {
        Assertions.assertThrows(UpdateNonExistObjectException.class,
                () -> userService.getUser(1),
                "При добавлении пользователя ожидалось UpdateNonExistObjectException");
    }

    @Test
    public void getEmptyListFriends() {
        userService.addUser(createUser(1 + "user@e.mail", 1 + "login", 1 + "name",
                2000, 1, 1));

        Assertions.assertTrue(userService.getFriends(1).isEmpty(),
                "Список друзей не пустой");
    }

    @Test
    public void getFillListFriends() {
        fillUsers();
        userService.addFriend(1, 2);
        userService.addFriend(1, 5);

        Assertions.assertFalse(userService.getFriends(1).isEmpty(),
                "Список друзей пользователя 1 пустой");
        Assertions.assertFalse(userService.getFriends(2).isEmpty(),
                "Список друзей пользователя 2 пустой");
        Assertions.assertFalse(userService.getFriends(5).isEmpty(),
                "Список друзей пользователя 5 пустой");
        Assertions.assertEquals(2, userService.getFriends(1).get(0).getId(),
                "Список друзей неправильно заполнен");
        Assertions.assertEquals(5, userService.getFriends(1).get(1).getId(),
                "Список друзей неправильно заполнен");
        Assertions.assertEquals(1, userService.getFriends(2).get(0).getId(),
                "Список друзей неправильно заполнен");
        Assertions.assertEquals(1, userService.getFriends(5).get(0).getId(),
                "Список друзей неправильно заполнен");
    }

    @Test
    public void getFriendsIncorrectId() {
        Assertions.assertThrows(UpdateNonExistObjectException.class,
                () -> userService.getFriends(1),
                "При получении друзей пользователя ожидалось UpdateNonExistObjectException");
    }

    @Test
    public void getEmptySameFriends() {
        fillUsers();

        Assertions.assertTrue(userService.getSameFriends(1, 2).isEmpty(),
                "Список общих друзей должен быть пустым");
    }

    @Test
    public void getFillSameFriends() {
        fillUsers();
        userService.addFriend(1, 2);
        userService.addFriend(1, 5);

        Assertions.assertFalse(userService.getSameFriends(2, 5).isEmpty(),
                "Список общих друзей должен быть заполнен");
        Assertions.assertEquals(1, userService.getSameFriends(2, 5).get(0).getId(),
                "Список общих друзей должен быть заполнен");

    }

    @Test
    public void getSameFriendsIncorrectUserId() {
        Assertions.assertThrows(UpdateNonExistObjectException.class,
                () -> userService.getSameFriends(1, 2),
                "При получении общих друзей ожидалось UpdateNonExistObjectException");
    }

    @Test
    public void getSameFriendsIncorrectFriendId() {
        userService.addUser(createUser(1 + "user@e.mail", 1 + "login", 1 + "name",
                2000, 1, 1));

        Assertions.assertThrows(UpdateNonExistObjectException.class,
                () -> userService.getSameFriends(1, 2),
                "При получении общих друзей ожидалось UpdateNonExistObjectException");
    }

    @Test
    public void addFirstUser() {
        userService.addUser(createUser(1 + "user@e.mail", 1 + "login", 1 + "name",
                2000, 1, 1));

        Assertions.assertEquals(createUser(1, 1 + "user@e.mail", 1 + "login", 1 + "name",
                        2000, 1, 1, new HashSet<>()),
                userService.getUser(1),
                "Первый созданный пользователь неправильно добавляется");
    }

    @Test
    public void addNewUser() {
        fillUsers();

        userService.addUser(createUser(21 + "user@e.mail", 21 + "login", 21 + "name",
                2000, 1, 21));

        Assertions.assertEquals(createUser(21, 21 + "user@e.mail", 21 + "login", 21 + "name",
                        2000, 1, 21, new HashSet<>()),
                userService.getUser(21),
                "Новый пользователь неправильно добавляется");
    }

    @Test
    public void addExistUser() {
        fillUsers();

        Assertions.assertThrows(AddExistObjectException.class,
                () -> userService.addUser(createUser(1, 1 + "user@e.mail", 1 + "login", 1 + "name",
                        2000, 1, 1)),
                "При добавлнении пользователя ожидалось AddExistObjectException");
    }

    @Test
    public void addUserWithoutOptionalParameters() {
        userService.addUser(createUser(1 + "user@e.mail", 1 + "login", null,
                2000, 1, 1));

        Assertions.assertEquals(createUser(1, 1 + "user@e.mail", 1 + "login", 1 + "login",
                        2000, 1, 1, new HashSet<>()),
                userService.getUser(1),
                "Необазятельные параметры пользователя неправильно обрабатываются");
    }


    @Test
    public void updateExistUser() {
        userService.addUser(createUser(1 + "user@e.mail", 1 + "login", 1 + "name",
                2000, 1, 1));

        Assertions.assertEquals(createUser(1, 2 + "user@e.mail", 2 + "login", 2 + "name",
                        2000, 1, 2, new HashSet<>()),
                userService.updateUser(createUser(1, 2 + "user@e.mail", 2 + "login", 2 + "name",
                        2000, 1, 2)),
                "Пользователь неправильно обновляется");
    }

    @Test
    public void updateNonExistUser() {
        Assertions.assertThrows(UpdateNonExistObjectException.class,
                () -> userService.updateUser(createUser(1, 1 + "user@e.mail", 1 + "login",
                        1 + "name", 2000, 1, 1)),
                "При обновлении пользователя ожидалось UpdateNonExistObjectException");
    }

    @Test
    public void addFriendCorrect() {
        fillUsers();
        userService.addFriend(1, 2);

        Assertions.assertEquals(2, userService.getFriends(1).get(0).getId(),
                "Добавление друзей работает неправильно");
    }

    @Test
    public void addFriendSameId() {
        fillUsers();

        Assertions.assertThrows(IncorrectObjectModificationException.class,
                () -> userService.addFriend(1, 1),
                "При добавлении друга ожидалось IncorrectObjectModificationException");
    }

    @Test
    public void addFriendIncorrectUserId() {
        Assertions.assertThrows(UpdateNonExistObjectException.class,
                () -> userService.addFriend(1, 2),
                "При добавлении друга ожидалось UpdateNonExistObjectException");
    }

    @Test
    public void addFriendInCorrectFriendId() {
        fillUsers();

        Assertions.assertThrows(UpdateNonExistObjectException.class,
                () -> userService.addFriend(1, 21),
                "При добавлении друга ожидалось UpdateNonExistObjectException");
    }

    @Test
    public void deleteExistUser() {
        userService.addUser(createUser(1, 1 + "user@e.mail", 1 + "login", 1 + "name",
                2000, 1, 1));

        Assertions.assertEquals(createUser(1, 1 + "user@e.mail", 1 + "login", 1 + "name",
                        2000, 1, 1, new HashSet<>()),
                userService.deleteUser(1),
                "Пользователь неправильно удаляется");
    }

    @Test
    public void deleteNonExistUser() {
        Assertions.assertThrows(UpdateNonExistObjectException.class,
                () -> userService.deleteUser(1),
                "При удалении пользователя ожидалось UpdateNonExistObjectException");
    }

    @Test
    public void deleteFriendCorrect() {
        fillUsers();
        userService.addFriend(1, 2);

        Assertions.assertTrue(userService.deleteFriend(1, 2).getFriends().isEmpty(),
                "Удаление друзей работает неправильно");
        Assertions.assertTrue(userService.getUser(2).getFriends().isEmpty(),
                "Удаление друзей работает неправильно");
    }

    @Test
    public void deleteFriendSameId() {
        fillUsers();

        Assertions.assertThrows(IncorrectObjectModificationException.class,
                () -> userService.deleteFriend(1, 1),
                "При удалении друга ожидалось IncorrectObjectModificationException");
    }

    @Test
    public void deleteFriendIncorrectUserId() {
        Assertions.assertThrows(UpdateNonExistObjectException.class,
                () -> userService.addFriend(1, 2),
                "При удалении друга ожидалось UpdateNonExistObjectException");
    }

    @Test
    public void deleteFriendInCorrectFriendId() {
        fillUsers();

        Assertions.assertThrows(UpdateNonExistObjectException.class,
                () -> userService.addFriend(1, 21),
                "При удалении друга ожидалось UpdateNonExistObjectException");
    }
}
