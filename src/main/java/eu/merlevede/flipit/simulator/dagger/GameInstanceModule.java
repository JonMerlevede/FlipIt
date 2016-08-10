package eu.merlevede.flipit.simulator.dagger;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import eu.merlevede.flipit.simulator.Clock;
import eu.merlevede.flipit.simulator.GameConfiguration;
import eu.merlevede.flipit.simulator.resource.MutableResource;
import eu.merlevede.flipit.simulator.resource.MutableResourceLeaf;
import eu.merlevede.flipit.simulator.actors.*;
import eu.merlevede.flipit.simulator.strategies.Strategy;
import eu.merlevede.flipit.simulator.valuator.Valuator;

import javax.inject.Named;
import java.util.Random;

/**
 * Created by jonat on 16/04/2016.
 */
@Module
public class GameInstanceModule {
    public GameInstanceModule() { }
    // Strategy state is injected automatically (constructor injection)

    @Provides
    @GameInstanceScope
    public Clock provideClock(GameConfiguration gc) {
        return new Clock(gc.getTotalSimulationDuration());
    }

    @Provides
    @GameInstanceScope
    public MutableResource provideResource(@ForDefender Player defender, Clock clock, Random random) {
        return new MutableResourceLeaf(defender, clock, random);
    }

    @Provides
    @ForDefender
    public Strategy provideDefenderStrategy(GameConfiguration gc, Random random) {
        return gc.getDefenderStrategyFactory().create(random);
    }

    @Provides
    @ForAttacker
    public Strategy provideAttackerStrategy(GameConfiguration gc, Random random) {
        return gc.getAttackerStrategyFactory().create(random);
    }

    @Provides
    @GameInstanceScope
    public Scheduler provideScheduler() {
        return Schedulers.create(2);
    }

    @Provides
    @IntoSet
    @GameInstanceScope
    public Actor defenderActor(@ForDefender Player defender) {
        return defender;
    }

    @Provides
    @IntoSet
    @GameInstanceScope
    public Actor attackerActor(@ForAttacker Player attacker) {
        return attacker;
    }

    @Provides
    @ForDefender
    @GameInstanceScope
    public Player provideDefender(@ForDefender Strategy strategy, @ForDefender Valuator valuator, Scheduler scheduler) {
        return Player.create(strategy, valuator, PlayerType.DEFENDER, scheduler);
    }

    @Provides
    @ForAttacker
    @GameInstanceScope
    public Player provideAttacker(@ForAttacker Strategy strategy, @ForAttacker Valuator valuator, Scheduler scheduler) {
        return Player.create(strategy, valuator, PlayerType.ATTACKER, scheduler);
    }


    @Provides
    @GameInstanceScope
    public Random provideRandom(@Named ("gameScope") Random random) {
        return new Random(random.nextInt());
    }
}

