package eu.merlevede.flipit.simulator;

import eu.merlevede.flipit.simulator.strategies.Strategy;

import java.util.Random;

/**
 * Created by jonat on 25/05/2016.
 */
@FunctionalInterface
public interface StrategyFactory {
    Strategy create(Random random);
}
