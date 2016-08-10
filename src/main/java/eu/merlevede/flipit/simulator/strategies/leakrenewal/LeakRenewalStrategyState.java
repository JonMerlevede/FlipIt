package eu.merlevede.flipit.strategies.leakrenewal;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import eu.merlevede.flipit.actors.LeakEvent;
import eu.merlevede.flipit.actors.Player;

import java.util.Random;

/**
 * Created by Jonathan on 20/04/2016.
 */
public final class LeakRenewalStrategyState extends RenewalStrategyState {
    private double lastLeak;
    private double lastActTime;
    private Player lastLeakOwner;

    public LeakRenewalStrategyState(Random random, EventBus leakEventBus) {
        super(random);
        this.lastLeak = Double.NaN;
        leakEventBus.register(this);
    }

    @Subscribe
    public void handleLeakEvent(LeakEvent leakEvent) {
        Preconditions.checkArgument(lastLeak == Double.NaN || leakEvent.getTime() >= lastLeak);
        lastLeak = leakEvent.getTime();
        lastLeakOwner = leakEvent.getCurrentOwner();
    }

    public boolean isLeakAct() {
        return isInitialized()
                && !Double.isNaN(lastLeak)
                && (Double.isNaN(getLastActTime()) || getLastLeakTime() > getLastActTime());
    }

    public double getLastActTime() {
        return this.lastActTime;
    }

    public void setLastActTime() {
        this.lastActTime = lastActTime;
    }

    public double getLastLeakTime() {
        Preconditions.checkState(isLeakAct());
        return this.lastLeak;
    }

    public Player getLastLeakOwner() {
        Preconditions.checkState(isLeakAct());
        return lastLeakOwner;
    }
}
