package eu.merlevede.flipit.simulator.resource;

import eu.merlevede.flipit.simulator.Clock;
import eu.merlevede.flipit.simulator.ClockReader;
import eu.merlevede.flipit.simulator.actors.Player;

/**
 * Created by jonat on 12/04/2016.
 */
public interface ReadableResource extends Resource {
    /**
     * The clock associated with this resouce.
     */
    ClockReader getClock();

    /**
     * Returns the player that was the owner of the resource epsilon time ago, with epsilon arbitrarily small.
     *
     * @see #getClock() The clock that provides this resource with a notion of time.
     */
    Player getOwner();
}
