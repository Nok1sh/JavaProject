package org.project.model.parser;

import org.project.model.db.DatabaseManager;
import org.project.model.db.entities.PlayersEntity;
import org.project.model.db.entities.PositionsEntity;
import org.project.model.db.entities.TeamsEntity;
import org.project.model.player.Player;
import org.project.model.player.Position;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;

public class CsvParser {

    private String path;

    public CsvParser(String path) {
        this.path = path;
    }

//    public List<Player> parseCsv() throws IOException {
//        return Files.readAllLines(Paths.get(path))
//                .stream()
//                .skip(1)
//                .map(CsvParser::parseLine)
//                .toList();
//    }

    public void parseCsv() throws IOException, SQLException {
        String path = "data/table.csv";
        Path csvPath = Path.of(path).toAbsolutePath();

        if (!DatabaseManager.isPlayersTableEmpty() && !Files.exists(csvPath)){
            return;
        }
        System.out.println("Parsing csv...");
        Files.readAllLines(Paths.get(path))
                .stream()
                .skip(1)
                .map(CsvParser::parseLine)
                .forEach(entity -> {
                    try {
                        DatabaseManager.savePlayers(entity);
                    } catch (SQLException e) {
                        throw new RuntimeException("Ошибка при сохранении игрока: " + entity.getName(), e);
                    }
                });
    }

    private static PlayersEntity parseLine(String line) {
        var row = line.split(",");
        String teamName = row[1].trim().replaceAll("[\" ]+", "");
        String positionName = row[2].trim().replaceAll("[\" ]+", "");
        
        try {
            TeamsEntity teamEntity = DatabaseManager.getTeamsDao().queryBuilder()
                    .where().eq("team", teamName).query().stream().findFirst()
                    .orElse(null);
            
            if (teamEntity == null) {
                teamEntity = new TeamsEntity(teamName);
                DatabaseManager.saveTeams(teamName);
            }
            
            PositionsEntity positionEntity = DatabaseManager.getPositionsDao().queryBuilder()
                    .where().eq("position", Position.valueOf(positionName)).query().stream().findFirst()
                    .orElse(null);
            
            if (positionEntity == null) {
                positionEntity = new PositionsEntity(Position.valueOf(positionName));
                DatabaseManager.savePositions(Position.valueOf(positionName));
            }
            
            return new PlayersEntity(
                    row[0],
                    teamEntity,
                    positionEntity,
                    Integer.parseInt(row[3]),
                    Integer.parseInt(row[4]),
                    Double.parseDouble(row[5])
            );
            
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при сохранении игрока", e);
        }
//        return new Player(
//                row[0],
//                row[1].trim().replaceAll("[\" ]+", ""),
//                Position.valueOf(row[2].trim().replaceAll("[\" ]+", "")),
//                Integer.parseInt(row[3]),
//                Integer.parseInt(row[4]),
//                Double.parseDouble(row[5])
//        );
    }
}
