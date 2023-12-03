package ru.yandex.practicum.filmorate.storage.director;

        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.jdbc.core.JdbcTemplate;
        import org.springframework.jdbc.support.GeneratedKeyHolder;
        import org.springframework.jdbc.support.KeyHolder;
        import org.springframework.stereotype.Component;
        import ru.yandex.practicum.filmorate.exception.exceptions.AddExistObjectException;
        import ru.yandex.practicum.filmorate.exception.exceptions.UpdateNonExistObjectException;
        import ru.yandex.practicum.filmorate.model.Director;

        import java.sql.PreparedStatement;
        import java.sql.ResultSet;
        import java.sql.SQLException;
        import java.util.List;
        import java.util.Objects;

@Component("dBDirectorStorage")
public class DBDirectorStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DBDirectorStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Director> getAllDirectors() {
        String sqlQuery = "SELECT * FROM directors;";
        List<Director> result = jdbcTemplate.query(sqlQuery, DBDirectorStorage::createDirector);
        return result;
    }

    @Override
    public Director getDirector(int directorID) {
        return checkContainsDirector(directorID);
    }

    @Override
    public Director addDirector(Director director) {
        checkNonContainsDirector(director.getId());

        String sqlQuery = "INSERT INTO directors (name)" +
                "VALUES (?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"director_id"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);

        director.setId((int) Objects.requireNonNull(keyHolder.getKey()));

        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        checkContainsDirector(director.getId());

        String sqlQuery = "UPDATE directors " +
                "SET name = ?" +
                "WHERE director_id = ?;";

        jdbcTemplate.update(sqlQuery,
                director.getName(),
                director.getId()
        );

        return director;
    }

    @Override
    public Director deleteDirector(int directorId) {
        Director director = checkContainsDirector(directorId);
        String sqlQuery = "DELETE FROM directors WHERE director_id = ?;";

        jdbcTemplate.update(sqlQuery, directorId);

        return director;
    }

    private void checkNonContainsDirector(int directorId) {
        String sqlQuery = "SELECT * FROM directors WHERE director_id = ?;";
        List<Director> director = jdbcTemplate.query(sqlQuery, DBDirectorStorage::createDirector, directorId);

        if (director.size() > 0) {
            throw new AddExistObjectException("Director с указанным id = " + directorId + " уже существует");
        }
    }

    public Director checkContainsDirector(int directorId) {
        String sqlQuery = "SELECT * FROM directors WHERE director_id = ?;";
        List<Director> director = jdbcTemplate.query(sqlQuery, DBDirectorStorage::createDirector, directorId);

        if (director.size() != 1) {
            throw new UpdateNonExistObjectException("Director с указанным id = " + directorId + " не существует");
        }

        return director.get(0);
    }

    public static Director createDirector(ResultSet resultSet, int rowNum) throws SQLException {
        Director director = new Director();
        director.setId(resultSet.getInt("director_id"));
        director.setName(resultSet.getString("name"));
        return director;
    }
}