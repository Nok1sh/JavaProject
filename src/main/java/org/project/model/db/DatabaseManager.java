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
    private static final String DATABASE_PLAYERS_URL = "jdbc:sqlite:players.db";
    private static final String DATABASE_TEAMS_URL = "jdbc:sqlite:teams.db";
    private static final String DATABASE_POSITIONS_URL = "jdbc:sqlite:positions.db";

    private static Dao<PlayersEntity, Integer> playersEntities;
    private Dao<TeamsEntity, Integer> teamsEntities;
    private Dao<PositionsEntity, Integer> positionsEntities;

    public void init() throws SQLException {
        ConnectionSource playersConnectionSource = new JdbcConnectionSource(DATABASE_PLAYERS_URL);
        playersEntities = DaoManager.createDao(playersConnectionSource, PlayersEntity.class);
        TableUtils.createTableIfNotExists(playersConnectionSource, PlayersEntity.class);

//        ConnectionSource teamsConnectionSource = new JdbcConnectionSource(DATABASE_TEAMS_URL);
//        teamsEntities = DaoManager.createDao(teamsConnectionSource, TeamsEntity.class);
//        TableUtils.createTableIfNotExists(teamsConnectionSource, TeamsEntity.class);
//
//        ConnectionSource positionsConnectionSource = new JdbcConnectionSource(DATABASE_POSITIONS_URL);
//        positionsEntities = DaoManager.createDao(positionsConnectionSource, PositionsEntity.class);
//        TableUtils.createTableIfNotExists(positionsConnectionSource, PositionsEntity.class);
    }

    public static void savePlayers(PlayersEntity entity) throws SQLException {
        playersEntities.create(entity);
    }

    public static boolean isPlayersTableEmpty() throws SQLException {
        return playersEntities.countOf() == 0;
    }

    public void saveTeams(String name) throws SQLException {
        teamsEntities.create(new TeamsEntity(name));
    }

    public void savePositions(Position position) throws SQLException {
        positionsEntities.create(new PositionsEntity(position));
    }

    public static List<Player> getAllPlayers() throws SQLException {
        List<PlayersEntity> entities = playersEntities.queryForAll();
        return entities.stream()
                .map(entity -> new Player(
                        entity.getName(),
                        entity.getTeam(),
                        entity.getPosition(),
                        entity.getHeight(),
                        entity.getWeight(),
                        entity.getAge()
                ))
                .toList();
    }
}
