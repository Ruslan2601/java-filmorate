package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.exceptions.UpdateNonExistObjectException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("dBMpaStorage")
public class DBMpaStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DBMpaStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<Integer, Mpa> getAllMpa() {
        String sqlQuery = "SELECT * FROM mpa;";
        List<Mpa> result = jdbcTemplate.query(sqlQuery, DBMpaStorage::createMpa);
        Map<Integer, Mpa> mpa = new HashMap<>();

        for (Mpa item : result) {
            mpa.put(item.getId(), item);
        }

        return mpa;
    }

    @Override
    public Mpa getMpa(int mpaId) {
        return checkContainsMpa(mpaId);
    }

    public static Mpa createMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return new Mpa(resultSet.getInt("mpa_id"), resultSet.getString("name"));
    }

    private Mpa checkContainsMpa(int mpaId) {
        String sqlQuery = "SELECT * FROM mpa WHERE mpa_id = ?;";
        List<Mpa> result = jdbcTemplate.query(sqlQuery, DBMpaStorage::createMpa, mpaId);

        if (result.size() != 1) {
            throw new UpdateNonExistObjectException("Mpa с указанным id = " + mpaId + " не существует " +
                    "или имееться больше 1");
        }

        return result.get(0);
    }
}
