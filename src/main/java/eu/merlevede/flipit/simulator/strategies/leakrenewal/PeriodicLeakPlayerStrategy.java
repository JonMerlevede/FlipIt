package eu.merlevede.flipit.strategies.leakrenewal;

import eu.merlevede.flipit.actors.Player;
import eu.merlevede.flipit.strategies.Strategy;

/**
 * Created by jonat on 12/04/2016.
 */
public final class PeriodicLeakPlayerStrategy implements Strategy<LeakRenewalStrategyState> {
    private final double period;

    public PeriodicLeakPlayerStrategy(double period) {
        this.period = period;
    }

    @Override
    public void act(final LeakRenewalStrategyState state, final Player.Actions actions) {
        double currentTime = actions.getCurrentTime();
        // Never act more than once at the same point in time
        if (state.getLastLeakTime() == currentTime) {
            return;
        }
        // If this is the first time we move
        if (state.isLeakAct()) {
            // ... then flip the resource if the leak says that we do not own it.
            if (state.getLastLeakOwner() != actions.getPlayer()) {
                actions.flip();
            }
        // If this is an act triggered by our previous act
        } else {
            actions.flip();
        }
        actions.setNextAct(currentTime + period);
    }

    @Override
    public void init(LeakRenewalStrategyState state, Player.Actions actions) {
        actions.setNextAct(state.getRandom().nextDouble()*period);
    }
}
