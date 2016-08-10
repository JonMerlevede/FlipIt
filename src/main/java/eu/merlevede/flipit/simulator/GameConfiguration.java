package eu.merlevede.flipit.simulator;

import org.inferred.freebuilder.FreeBuilder;

/**
 * Full, immutable description (configuration) of a sequence of FlipIt games.
 * Can be used to initialize {@link Game FlipIt game instances}.
 *
 * The game configuration class may be extended in the future to support e.g. additional players or multiple resources,
 * but when extending defaults should be provided to maintain backwards compatibility.
 *
 * Created by Jonathan on 4/05/2016.
 */
@FreeBuilder
public interface GameConfiguration {
    /**
     * A factory of defender strategies, i.e. a supplier of objects that define when the defender acts.
     */
    StrategyFactory getDefenderStrategyFactory();

    /**
     * Like {@link #getDefenderStrategyFactory()} but for the attacker.
     */
    StrategyFactory getAttackerStrategyFactory();

    /**
     * A factory of attacker valuators, i.e. a supplier of objects that indicate how the attacker valuates time.
     * The default way to valuate time is to take the average gains and costs over the simulated time frame, in
     * an attempt to approximate the limit-of-means definition that is given in the original FlipIt paper.
     */
    ValuatorFactory getAttackerValuatorFactory();

    /**
     * Like {@link #getAttackerValuatorFactory()} but for the attacker.
     */
    ValuatorFactory getDefenderValuatorFactory();
    double getTotalSimulationDuration();
    int getSeed();

    /**
     * Builder for game configurations.
     */
    class Builder extends GameConfiguration_Builder {
        public Builder() {
            setAttackerValuatorFactory(ValuatorFactories.mean());
            setDefenderValuatorFactory(ValuatorFactories.mean());
            setSeed(1);
        }
    }
}
