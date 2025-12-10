package org.project.model.calculate;

import org.project.model.player.Player;

import java.util.List;
import java.util.Map;

public interface IResolver {

    Map<String, Double> calculateAverageAge();

    Map<String, Double> calculateAverageHeight();

    Map<String, Double> calculateAverageWeight();

    List<Player> calculate5HighestPlayer();

    String getTeamWithHighestAverageAge();
}
