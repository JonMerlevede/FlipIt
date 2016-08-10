package eu.merlevede.flipit.simulator.strategies;

import eu.merlevede.flipit.simulator.strategies.generators.PeriodicGenerator;

import java.util.Random;

/**
 * Class full of static utility methods for creating and initializing nonadaptive strategies.
 *
 * Created by jonat on 23/05/2016.
 */
public final class NAStrategies {
    public static NAStrategy periodic(final Random random, final double period) {
        return new NAStrategy(new PeriodicGenerator(random, period));
    }
}
