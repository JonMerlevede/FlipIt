package eu.merlevede.flipit.simulator;

import eu.merlevede.flipit.simulator.valuator.MeanValuator;

/**
 * Static factory methods pertaining to {@link ValuatorFactory} instances.
 *
 * Created by jonat on 25/05/2016.
 */
public final class ValuatorFactories {
    private ValuatorFactories() {}
    private static ValuatorFactory free = (totalSimulationTime) -> MeanValuator.create(totalSimulationTime);
    public static ValuatorFactory mean(double flipCost) {
        return (totalSimulationTime) -> MeanValuator.create(flipCost, totalSimulationTime);
    }
    public static ValuatorFactory mean() {
        return free;
    }
}
