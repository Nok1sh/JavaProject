package org.project.resolver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.project.model.calculate.Resolver;
import org.project.model.player.Player;
import org.project.model.player.Position;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ResolverTest {

    private Resolver resolver;

    @BeforeEach
    void setUp() throws SQLException {
        List<Player> testPlayers = List.of(
            new Player("Player1", "TeamA", Position.Catcher, 75, 200, 25.0),
            new Player("Player2", "TeamA", Position.Catcher, 76, 205, 30.0),
            new Player("Player3", "TeamB", Position.Catcher, 74, 195, 28.0),
            new Player("Player4", "TeamB", Position.Catcher, 78, 210, 32.0),
            new Player("Player5", "TeamC", Position.Catcher, 73, 185, 26.0),
            new Player("Player6", "TeamC", Position.Catcher, 79, 215, 29.0)
        );
        
        resolver = new Resolver() {
            {
                try {
                    java.lang.reflect.Field playersField = Resolver.class.getDeclaredField("players");
                    playersField.setAccessible(true);
                    playersField.set(this, testPlayers);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @Test
    void calculateAverageAge_ReturnsCorrectAverages() {
        Map<String, Double> result = resolver.calculateAverageAge();
        assertEquals(27.5, result.get("TeamA"), 0.001);
        assertEquals(30.0, result.get("TeamB"), 0.001);
        assertEquals(27.5, result.get("TeamC"), 0.001);
    }

    @Test
    void calculateAverageHeight_ReturnsCorrectAverages() {
        Map<String, Double> result = resolver.calculateAverageHeight();
        assertEquals(75.5, result.get("TeamA"), 0.01);
        assertEquals(76.0, result.get("TeamB"), 0.01);
        assertEquals(76.0, result.get("TeamC"), 0.01);
    }

    @Test
    void calculateAverageWeight_ReturnsCorrectAverages() {
        Map<String, Double> result = resolver.calculateAverageWeight();
        assertEquals(202.5, result.get("TeamA"), 0.01);
        assertEquals(202.5, result.get("TeamB"), 0.01);
        assertEquals(200.0, result.get("TeamC"), 0.01);
    }

    @Test
    void calculate5HighestPlayer_ReturnsFiveOldestPlayersFromTeamWithHighestAverageAge() {
        List<Player> result = resolver.calculate5HighestPlayer();
        assertEquals("TeamB", result.get(0).team());
        assertEquals(32, result.get(0).age());
        assertEquals(28, result.get(1).age());
        assertEquals(2, result.size());
    }

    @Test
    void calculate5HighestPlayer_NoPlayersInTeam_ReturnsEmptyList() throws SQLException {
        List<Player> testPlayers = List.of(
            new Player("Player1", null, Position.Catcher, 75, 200, 35.0),
            new Player("Player2", null, Position.Catcher, 76, 205, 30.0)
        );
        
        Resolver emptyResolver = new Resolver() {
            {
                try {
                    java.lang.reflect.Field playersField = Resolver.class.getDeclaredField("players");
                    playersField.setAccessible(true);
                    playersField.set(this, testPlayers);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        
        List<Player> result = emptyResolver.calculate5HighestPlayer();
        assertTrue(result.isEmpty(), "Список игроков должен быть пустым, когда в командах нет игроков");
    }

    @Test
    void getTeamWithHighestAverageAge_ReturnsTeamMeetingCriteria() {
        String result = resolver.getTeamWithHighestAverageAge();
        assertEquals("TeamB", result);
    }

    @Test
    void getTeamWithHighestAverageAge_NoTeamsWithPlayers_ReturnsNull() throws SQLException {
        List<Player> testPlayers = List.of(
            new Player("Player1", null, Position.Catcher, 75, 200, 25.0),
            new Player("Player2", null, Position.Catcher, 76, 205, 30.0)
        );
        
        Resolver noTeamsResolver = new Resolver() {
            {
                try {
                    java.lang.reflect.Field playersField = Resolver.class.getDeclaredField("players");
                    playersField.setAccessible(true);
                    playersField.set(this, testPlayers);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        
        String result = noTeamsResolver.getTeamWithHighestAverageAge();
        assertNull(result);
    }

    @Test
    void getTeamWithHighestAverageAge_NoTeamMeetsCriteria_ReturnsNull() throws SQLException {
        List<Player> testPlayers = List.of(
            new Player("Player1", "TeamA", Position.Catcher, 70, 180, 25.0),
            new Player("Player2", "TeamA", Position.Catcher, 71, 185, 30.0)
        );
        
        Resolver resolverWithNoMatch = new Resolver() {
            {
                try {
                    java.lang.reflect.Field playersField = Resolver.class.getDeclaredField("players");
                    playersField.setAccessible(true);
                    playersField.set(this, testPlayers);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        
        String result = resolverWithNoMatch.getTeamWithHighestAverageAge();
        assertNull(result);
    }
}
