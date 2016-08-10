package eu.merlevede.flipit.simulator;

import eu.merlevede.flipit.simulator.dagger.*;


/**
 * Created by Jonathan on 22/04/2016.
 */
public class TestHelper {
    static GameInstanceComponent getTestComponent() {
        GameConfiguration conf = new GameConfiguration.Builder()
                .setDefenderStrategyFactory(StrategyFactories.periodic(1))
                .setAttackerStrategyFactory(StrategyFactories.periodic(2))
                .setDefenderValuatorFactory(ValuatorFactories.mean(1))
                .setAttackerValuatorFactory(ValuatorFactories.mean(1))
                .setTotalSimulationDuration(100)
                .build();
        Game.supplier(conf);
        GameModule module = GameModule.create(conf);
        GameComponent gc = DaggerGameComponent.builder().gameModule(module).build();
        return gc.gameInstanceComponent();
    }
}
