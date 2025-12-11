package org.project.model.calculate;

import org.project.model.db.DatabaseManager;
import org.project.model.player.Player;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Resolver implements IResolver {

    private List<Player> players;

    public Resolver() throws SQLException {
        this.players = DatabaseManager.getAllPlayers();
    }

    @Override
    public Map<String, Double> calculateAverageAge() {
        return players.stream()
                .filter(player -> player.team() != null)
                .collect(Collectors.groupingBy(
                        Player::team,
                        Collectors.collectingAndThen(
                                Collectors.averagingDouble(Player::age),
                                avg -> Math.round(avg * 1000.0) / 1000.0
                        )
                ));
    }

    @Override
    public Map<String, Double> calculateAverageHeight() {
        return players.stream()
                .filter(player -> player.team() != null)
                .collect(Collectors.groupingBy(
                        Player::team,
                        Collectors.collectingAndThen(
                                Collectors.averagingInt(Player::height),
                                avg -> Math.round(avg * 100.0) / 100.0
                        )
                ));
    }

    @Override
    public Map<String, Double> calculateAverageWeight() {
        return players.stream()
                .filter(player -> player.team() != null)
                .collect(Collectors.groupingBy(
                        Player::team,
                        Collectors.collectingAndThen(
                                Collectors.averagingInt(Player::weight),
                                avg -> Math.round(avg * 100.0) / 100.0
                        )
                ));
    }

    private String getHighestTeam() {
        Map<String, Double> averageAges = calculateAverageAge();
        return averageAges.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(1)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Player> calculate5HighestPlayer() {
        String maxAverageAgeTeam = getHighestTeam();

        return players.stream()
                .filter(player -> player.team() != null && player.team().equals(maxAverageAgeTeam))
                .sorted(Comparator.comparingDouble(Player::age).reversed())
                .limit(5)
                .toList();
    }

    @Override
    public String getTeamWithHighestAverageAge() {
        Map<String, Double> averageAges = calculateAverageAge();
        Map<String, Double> averageHeights = calculateAverageHeight();
        Map<String, Double> averageWeights = calculateAverageWeight();

        return averageAges.entrySet().stream()
                .filter(map -> averageHeights.get(map.getKey()) >= 74
                        && averageHeights.get(map.getKey()) <= 78
                        && averageWeights.get(map.getKey()) >= 190
                        && averageWeights.get(map.getKey()) <= 210)
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }
}
