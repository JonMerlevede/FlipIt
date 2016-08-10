package eu.merlevede.flipit.simulator.actions;

import eu.merlevede.flipit.simulator.resource.Resource;
import eu.merlevede.flipit.simulator.actors.Actor;
import eu.merlevede.flipit.simulator.actors.Player;

/**
 * Actions that can be executed by players.
 *
 * Instances of Actions are used as arguments to e.g. methods defined by {@link eu.merlevede.flipit.simulator.strategies.Strategy player strategies}.
 * It is not allowed to keep a reference to an actions object alive beyond the method call.
 *
 * Use {@link ForwardingActions} for decorating this class.
 *
 * Created by Jonathan on 19/04/2016.
 */
public interface Actions {
    double getCurrentTime();
    default boolean isStarted() {
        return getCurrentTime() >= 0;
    }

    /**
     * The default resource associated with this set of actions.
     */
    Resource getResource();

    /**
     * Schedules the given actor at the given time.
     * At the given time, the given actor will be triggered by a {@link eu.merlevede.flipit.simulator.actions.Trigger.Scheduled} trigger.
     *
     * @param actor The given actor.
     * @param time The given time.
     */
    void schedule(Actor actor, double time);

    /**
     * Makes the given player flip the given resource.
     *
     * @param player    The given player.
     * @param resource  The given resource.
     */
    void flip(Player player, Resource resource);

    /**
     * Makes the given player flip this set of action's {@link #getResource() default resource}.
     *
     * @param player The given player.
     */
    default void flip(Player player) {
        flip(player, getResource());
    }
}
