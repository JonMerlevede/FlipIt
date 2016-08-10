package eu.merlevede.flipit.simulator.actions;

import com.google.common.collect.ForwardingObject;
import eu.merlevede.flipit.simulator.resource.Resource;
import eu.merlevede.flipit.simulator.actors.Actor;
import eu.merlevede.flipit.simulator.actors.Player;

/**
 * Utility class that is useful for decorating {@link Actions} classes.
 *
 * Created by jonat on 3/05/2016.
 */
public abstract class ForwardingActions extends ForwardingObject implements Actions {
    public abstract Actions delegate();

    @Override
    public double getCurrentTime() {
        return delegate().getCurrentTime();
    }

    @Override
    public boolean isStarted() {
        return delegate().isStarted();
    }

    @Override
    public Resource getResource() {
        return delegate().getResource();
    }

    @Override
    public void schedule(Actor actor, double time) {
        delegate().schedule(actor, time);
    }

    @Override
    public void flip(final Player player, final Resource resource) {
        delegate().flip(player, resource);
    }
}
