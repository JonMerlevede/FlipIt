package eu.merlevede.flipit.simulator;

import eu.merlevede.flipit.simulator.valuator.Valuator;

/**
 * Created by jonat on 25/05/2016.
 */
public interface ValuatorFactory {
    Valuator create(double totalSimulationDuration);
}
