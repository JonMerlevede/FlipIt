package eu.merlevede.flipit.theory;

import eu.merlevede.flipit.simulator.GameConfiguration;
import eu.merlevede.flipit.simulator.StrategyFactories;
import eu.merlevede.flipit.simulator.ValuatorFactories;
import eu.merlevede.flipit.simulator.actors.PlayerType;
import org.inferred.freebuilder.FreeBuilder;

/**
 * Created by jonat on 23/05/2016.
 */
public final class Periodic {
    private static void checkType(PlayerType type) {
        if (type != PlayerType.ATTACKER && type != PlayerType.DEFENDER) {
            throw new IllegalArgumentException();
        }
    }
    @FreeBuilder
    public interface Mean {
        double getDefenderPeriod();
        double getAttackerPeriod();
        double getDefenderFlipCost();
        double getAttackerFlipCost();

        default GameConfiguration.Builder toGameConfiguration() {
            return new GameConfiguration.Builder()
                .setDefenderStrategyFactory(StrategyFactories.periodic(getDefenderPeriod()))
                .setAttackerStrategyFactory(StrategyFactories.periodic(getAttackerPeriod()))
                .setDefenderValuatorFactory(ValuatorFactories.mean(getDefenderFlipCost()))
                .setAttackerValuatorFactory(ValuatorFactories.mean(getAttackerFlipCost()));
        }

        default double getPeriod(PlayerType type) {
            checkType(type);
            double result = type == PlayerType.DEFENDER ? getDefenderPeriod() : getAttackerPeriod();
            if (Double.isNaN(result)) { throw new UnsupportedOperationException(); }
            return result;
        }
        default double getFlipCost(PlayerType type) {
            checkType(type);
            double result = type == PlayerType.DEFENDER ? getDefenderFlipCost() : getAttackerFlipCost();
            if (Double.isNaN(result)) { throw new UnsupportedOperationException(); }
            return result;
        }
        default double cost(PlayerType type) {
            checkType(type);
            return getFlipCost(type)/getPeriod(type);
        }
        default double gain(PlayerType type) {
            checkType(type);
            double r = getDefenderPeriod() < getAttackerPeriod() ? getDefenderPeriod() / getAttackerPeriod() : getAttackerPeriod() / getDefenderPeriod();
            switch(type) {
                case DEFENDER:
                    return getDefenderPeriod() < getAttackerPeriod() ? 1 - r/2 : r/2;
                case ATTACKER:
                    return getDefenderPeriod() < getAttackerPeriod() ? r/2 : 1 - r/2;
            }
            throw new RuntimeException();
        }
        default double benefit(PlayerType type) {
            return gain(type) - cost(type);
        }

        class Builder extends Periodic_Mean_Builder {
            public Builder() {
                setAttackerFlipCost(0);
                setDefenderFlipCost(0);
                setAttackerPeriod(0);
                setDefenderPeriod(0);
            }
        }
    }
}
