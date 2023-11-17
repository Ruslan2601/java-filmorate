package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.exceptions.IncorrectObjectModificationException;
import ru.yandex.practicum.filmorate.exception.exceptions.UpdateNonExistObjectException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public List<Film> getAllFilms() {
        return new ArrayList<>(filmStorage.getAllFilms().values());
    }

    public Film getFilm(int filmId) {
        return filmStorage.getFilm(filmId);
    }

    public List<Film> getMostLikedFilms(int count) {
        List<Film> films = getAllFilms();
        List<Film> mostLikedFilms = new ArrayList<>(count);

        if (count > films.size()) {
            count = films.size();
        }

        for (int i = 0; i < count; ++i) {
            mostLikedFilms.add(films.get(i));
        }

        mostLikedFilms.sort((o1, o2) -> o2.getUserLikes().size() - o1.getUserLikes().size());

        for (int i = count; i < films.size(); ++i) {
            if (mostLikedFilms.get(count - 1).getUserLikes().size() >= films.get(i).getUserLikes().size()) {
                continue;
            }

            for (int j = 0; j < mostLikedFilms.size(); ++j) {
                if (films.get(i).getUserLikes().size() > mostLikedFilms.get(j).getUserLikes().size()) {
                    mostLikedFilms.remove(count - 1);
                    mostLikedFilms.add(j, films.get(i));
                    break;
                }
            }
        }

        return mostLikedFilms;
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film addLikeToFilm(int filmId, int userId) {
        Film film = filmStorage.getFilm(filmId);
        if (!film.getUserLikes().add(userId)) {
            throw new IncorrectObjectModificationException("Невозможно поставить уже существующий "
                    + "лайк пользователя с id = " + userId);
        }
        return film;
    }

    public Film deleteFilm(int filmId) {
        return filmStorage.deleteFilm(filmId);
    }

    public Film deleteLikeToFilm(int filmId, int userId) {
        Film film = filmStorage.getFilm(filmId);
        if (!film.getUserLikes().remove(userId)) {
            throw new UpdateNonExistObjectException("Невозможно удалить несуществующий "
                    + "лайк пользователя с id = " + userId);
        }
        return film;
    }
}