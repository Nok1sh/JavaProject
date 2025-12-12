package org.project.model.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.project.model.db.entities.PlayersEntity;
import org.project.model.db.entities.PositionsEntity;
import org.project.model.db.entities.TeamsEntity;
import org.project.model.player.Player;
import org.project.model.player.Position;

import java.sql.SQLException;
import java.util.List;

public class DatabaseManager {
    private static final String DATABASE_URL = "jdbc:sqlite:players_data.db";

    private static Dao<PlayersEntity, Integer> playersEntities;
    private static Dao<TeamsEntity, Integer> teamsEntities;
    private static Dao<PositionsEntity, Integer> positionsEntities;

    static {
        try {
            ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL);
            playersEntities = DaoManager.createDao(connectionSource, PlayersEntity.class);
            teamsEntities = DaoManager.createDao(connectionSource, TeamsEntity.class);
            positionsEntities = DaoManager.createDao(connectionSource, PositionsEntity.class);

            // Создаём таблицы, если их нет
            TableUtils.createTableIfNotExists(connectionSource, PlayersEntity.class);
            TableUtils.createTableIfNotExists(connectionSource, TeamsEntity.class);
            TableUtils.createTableIfNotExists(connectionSource, PositionsEntity.class);
        } catch (SQLException e) {
            throw new ExceptionInInitializerError("Ошибка инициализации БД: " + e.getMessage());
        }
    }

    public static void savePlayers(PlayersEntity entity) throws SQLException {
        playersEntities.create(entity);
    }

    public static Dao<PlayersEntity, Integer> getPlayersDao() {
        return playersEntities;
    }

    public static Dao<TeamsEntity, Integer> getTeamsDao() {
        return teamsEntities;
    }

    public static Dao<PositionsEntity, Integer> getPositionsDao() {
        return positionsEntities;
    }

    public static boolean isPlayersTableEmpty() throws SQLException {
        return playersEntities.countOf() == 0;
    }

    public static void saveTeams(String name) throws SQLException {
        teamsEntities.create(new TeamsEntity(name));
    }

    public static void savePositions(Position position) throws SQLException {
        positionsEntities.create(new PositionsEntity(position));
    }

    public static List<Player> getAllPlayers() throws SQLException {
        List<PlayersEntity> entities = playersEntities.queryForAll();

        for (PlayersEntity entity : entities) {
            if (entity.getTeam() != null) {
                teamsEntities.refresh(entity.getTeam());
            }
            if (entity.getPosition() != null) {
                positionsEntities.refresh(entity.getPosition());
            }
        }

        return entities.stream()
                .map(entity -> new Player(
                        entity.getName(),
                        entity.getTeam() != null ? entity.getTeam().getTeam() : null,
                        entity.getPosition() != null ? entity.getPosition().getPosition() : null,
                        entity.getHeight(),
                        entity.getWeight(),
                        entity.getAge()
                ))
                .toList();
    }
}
