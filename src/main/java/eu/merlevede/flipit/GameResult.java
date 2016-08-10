package eu.merlevede.flipit;

import eu.merlevede.flipit.simulator.actors.PlayerType;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by jonat on 20/05/2016.
 */
public interface GameResult {
    static String stringRepresentation(GameResult gameResult) {
        return gameResult.getPlayerResults().values().stream().map(pr -> pr.toString() + "\n").collect(Collectors.joining());
    }

    /**
     * Returns an unmodifiable map from players to player results.
     */
    Map<? extends PlayerType, ? extends PlayerResult> getPlayerResults();
}
