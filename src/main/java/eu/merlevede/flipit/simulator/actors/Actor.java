package eu.merlevede.flipit.simulator.actors;

import eu.merlevede.flipit.simulator.actions.Actions;
import eu.merlevede.flipit.simulator.actions.Trigger;

/**
 *
 *
 * Created by jonat on 12/04/2016.
 */
public interface Actor {
    void act(Trigger trigger, Actions actions);
}
