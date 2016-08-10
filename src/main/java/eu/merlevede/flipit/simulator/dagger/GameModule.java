package eu.merlevede.flipit.simulator.dagger;

import dagger.Module;
import dagger.Provides;
import eu.merlevede.flipit.simulator.GameConfiguration;
import eu.merlevede.flipit.simulator.valuator.Valuator;

import javax.inject.Named;
import java.util.Random;

/**
 * Created by jonat on 21/04/2016.
 */
@Module
public class GameModule {
    private final GameConfiguration gc;

    private GameModule( GameConfiguration gc) {
        this.gc = gc;
    }

    public static GameModule create(GameConfiguration gc) {
        return new GameModule(gc);
    }

    @Provides
    @GameScope
    public GameConfiguration provideGameConfiguration() { return gc; }

    @Provides
    @GameScope
    @ForDefender
    public Valuator provideDefenderValuator() {
        return gc.getDefenderValuatorFactory().create(gc.getTotalSimulationDuration());
    }

    @Provides
    @GameScope
    @ForAttacker
    public Valuator provideAttackerValuator() {
        return gc.getAttackerValuatorFactory().create(gc.getTotalSimulationDuration());
    }

    @Provides
    @GameScope
    @Named("gameScope")
    public Random provideRandom() {
        return new Random(gc.getSeed());
    }
}
