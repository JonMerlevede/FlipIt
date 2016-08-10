package eu.merlevede.flipit.simulator.resource;

import eu.merlevede.flipit.simulator.ClockReader;
import eu.merlevede.flipit.simulator.actors.Player;

import javax.inject.Inject;
import java.util.*;

/**
 * Created by jonat on 12/04/2016.
 */
public class MutableResourceLeaf implements MutableResource {
    private Player currentOwner;
    private int noFutureOwners;
    private Player futureOwner;
    private double lastChanged;
    private final ClockReader clock;
    private final Random random;

    @Inject
    public MutableResourceLeaf(Player startOwner, ClockReader clock, Random random) {
        this.currentOwner = startOwner;
        //this.futureOwners = new ArrayList<>(); // ideal data structure would be a set from which we can select a random element
        this.lastChanged = clock.getTime();
        this.clock = clock;
        this.random = random;
    }

    private void updateCurrentOwner() {
        assert noFutureOwners == 0 && futureOwner == null || noFutureOwners > 0 && futureOwner != null;
        if (isTimeIncreased() && futureOwner != null) {
            this.currentOwner = futureOwner;
            noFutureOwners = 0;
            futureOwner = null;
        }
    }

    private boolean isTimeIncreased() {
        if (clock.getTime() > lastChanged) {
            return true;
        } else if (clock.getTime() == lastChanged) {
            return false;
        }
        throw new IllegalStateException();
    }

    @Override
    public void setOwner(Player player) {
        noFutureOwners++;
        if (player == currentOwner || noFutureOwners == 1) {
            futureOwner = player;
        } else if (futureOwner != currentOwner) {
            double probability = 1/noFutureOwners;
            double r = random.nextDouble();
            if (probability > r) {
                futureOwner = player;
            }
        }
        lastChanged = clock.getTime();
    }

    @Override
    public MutableResource makeMutable(Resource resource) {
        if (!contains(resource)) { throw new IllegalArgumentException(); }
        return this;
    }

    @Override
    public ClockReader getClock() {
        return this.clock;
    }

    @Override
    public Player getOwner() {
        updateCurrentOwner();
        return currentOwner;
    }

    @Override
    public boolean contains(final Resource resource) {
        return resource == this;
    }
}
