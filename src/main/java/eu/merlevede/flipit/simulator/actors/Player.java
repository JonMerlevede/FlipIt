package eu.merlevede.flipit.simulator.actors;

import eu.merlevede.flipit.simulator.resource.MutableResource;
import eu.merlevede.flipit.simulator.resource.Resource;
import eu.merlevede.flipit.simulator.actions.Trigger;
import eu.merlevede.flipit.simulator.actions.ForwardingActions;
import eu.merlevede.flipit.simulator.actions.IllegalActionException;
import eu.merlevede.flipit.simulator.strategies.Strategy;
import eu.merlevede.flipit.simulator.valuator.Valuator;

/**
 * Created by jonat on 12/04/2016.
 */
public final class Player implements Actor {
    private final Valuator valuator;
    private final PlayerType type;
    private final Strategy strategy;

    private Player(Strategy strategy, Valuator valuator, PlayerType type, Scheduler scheduler) {
        this.valuator = valuator;
        this.strategy = strategy;
        this.type = type;
        double initTime = strategy.init();
        scheduler.schedule(this, initTime);
    }

    public static <T> Player create(Strategy strategy, Valuator valuator, PlayerType type, Scheduler scheduler) {
        return new Player(strategy, valuator, type, scheduler);
    }

    public Valuator getValuator() {
        return valuator;
    }

    public PlayerType getType() {
        return this.type;
    }

    @Override
    public void act(Trigger trigger, eu.merlevede.flipit.simulator.actions.Actions actions) {
        strategy.act(trigger, new ActionsWrapper(actions));
    }

    public interface Actions extends eu.merlevede.flipit.simulator.actions.Actions {
        /**
         * The player associated with this set of player actions.
         */
        Player getPlayer();

        /**
         * Makes the player associated with this player flip wrapped (see {@link #getPlayer()}) flip the given mutableResource.
         * @param resource  The given resource .
         */
        default void flip(Resource resource) {
            flip(getPlayer(), resource);
        }

        default void flip() {
            flip(getPlayer());
        }

        default void schedule(double time) {
            schedule(getPlayer(), time);
        }
    }

    private class ActionsWrapper extends ForwardingActions implements Player.Actions {
        private eu.merlevede.flipit.simulator.actions.Actions wrapped;

        public ActionsWrapper(final eu.merlevede.flipit.simulator.actions.Actions actions) {
            wrapped = actions;
        }
        @Override
        public eu.merlevede.flipit.simulator.actions.Actions delegate() {
            return wrapped;
        }

        @Override
        public Player getPlayer() {
            return Player.this;
        }

        @Override
        public void flip(Player player, Resource resource) {
            if (player != getPlayer()) {
                throw new IllegalActionException("Players cannot make other players perform flips");
            }
            super.flip(player, resource);
        }
    }
}
