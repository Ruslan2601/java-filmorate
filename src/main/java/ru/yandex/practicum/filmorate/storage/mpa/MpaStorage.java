package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Map;

public interface MpaStorage {
    Map<Integer, Mpa> getAllMpa();

    Mpa getMpa(int mpaId);
}
