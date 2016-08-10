package eu.merlevede.flipit.simulator.actions;

import com.google.auto.value.AutoValue;
import eu.merlevede.flipit.simulator.resource.Resource;
import eu.merlevede.flipit.simulator.actors.Player;

import java.util.OptionalDouble;

/**
 * A trigger is a reason (event) that indicates why a player's strategy is being called on.
 * Different strategies can respond to different triggers in different ways.
 *
 * An actor always has to specify at which point in time she wants to be activated next (because each player
 * always has such a point in time, the simulated time will keep progressing and no deadlock occurs). When this time
 * is comes, she is activated by a {@link Scheduled} trigger, the most common type of trigger.
 *
 * Non-adaptive strategies do not respond to any trigger but {@link Scheduled}.
 * Adaptive strategies can respond to information as it becomes available, e.g. they might occasionally be able to observe
 * the resource state and respond to this new information ({@link Leak}).
 *
 * Strategies can define how to respond to these different events by implementing a {@link TriggerVisitor}.
 *
 * In order to prevent deadlock, each player always has to have a Scheduled trigger scheduled. This is enforced in the
 * {@link TriggerVisitor}.
 *
 *
 * Created by Jonathan on 10/05/2016.
 */
public interface Trigger {
    OptionalDouble accept(TriggerVisitor visitor);

    /**
     * The schedule trigger is fired whenever the time has come for a player to execute the flip that she scheduled.
     *
     * E.g. when a player says "I want to flip 10 minutes from now", the scheduler will call upon the
     * player 10 minutes from now indicated that the scheduled event caused the trigger.
     */
    final class Scheduled implements Trigger {
        private static Scheduled instance = new Scheduled();
        private Scheduled() {}

        public static Scheduled create() {
            return instance;
        }

        @Override
        public OptionalDouble accept(TriggerVisitor visitor) {
            return OptionalDouble.of(visitor.scheduled());
        }
    }

    @AutoValue
    abstract class Leak implements Trigger {
        public static Leak create(Resource resource, Player owner) {
            return new AutoValue_Trigger_Leak(resource, owner);
        }
        Leak() { }

        public abstract Resource getResource();
        public abstract Player getOwner();

        @Override
        public OptionalDouble accept(TriggerVisitor visitor) {
            return visitor.visit(this);
        }
    }
}
