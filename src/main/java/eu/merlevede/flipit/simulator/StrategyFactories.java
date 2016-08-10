package eu.merlevede.flipit.simulator;

import eu.merlevede.flipit.simulator.strategies.NAStrategies;

/**
 * Static factory methods pertaining to {@link StrategyFactory} instances.
 *
 * Created by jonat on 25/05/2016.
 */
public final class StrategyFactories {
    private StrategyFactories() {}

    public static StrategyFactory periodic(double period) {
        return (random) -> NAStrategies.periodic(random, period);
    }
}
