package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.exceptions.UpdateNonExistObjectException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.DBMpaStorage;

import java.util.HashMap;
import java.util.Map;

@JdbcTest
public class DBMpaStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private final DBMpaStorage mpaStorage;

    @Autowired
    public DBMpaStorageTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaStorage = new DBMpaStorage(jdbcTemplate);
    }

    @Test
    public void getAllMpa() {
        Map<Integer, Mpa> mpa = new HashMap<>();
        mpa.put(1, new Mpa(1, "G"));
        mpa.put(2, new Mpa(2, "PG"));
        mpa.put(3, new Mpa(3, "PG-13"));
        mpa.put(4, new Mpa(4, "R"));
        mpa.put(5, new Mpa(5, "NC-17"));

        Assertions.assertEquals(mpa, mpaStorage.getAllMpa(),
                "Все mpa возвращаются неправильно");
    }

    @Test
    public void getExistMpa() {
        Assertions.assertEquals(new Mpa(1, "G"), mpaStorage.getMpa(1),
                "Mpa с указанынм id возвращается неправильно");
        Assertions.assertEquals(new Mpa(2, "PG"), mpaStorage.getMpa(2),
                "Mpa с указанынм id возвращается неправильно");
        Assertions.assertEquals(new Mpa(3, "PG-13"), mpaStorage.getMpa(3),
                "Mpa с указанынм id возвращается неправильно");
        Assertions.assertEquals(new Mpa(4, "R"), mpaStorage.getMpa(4),
                "Mpa с указанынм id возвращается неправильно");
        Assertions.assertEquals(new Mpa(5, "NC-17"), mpaStorage.getMpa(5),
                "Mpa с указанынм id возвращается неправильно");
    }

    @Test
    public void getNonExistMpa() {
        Assertions.assertThrows(UpdateNonExistObjectException.class,
                () -> mpaStorage.getMpa(0),
                "При добавлении фильма ожидалось UpdateNonExistObjectException");
    }
}
