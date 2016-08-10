package eu.merlevede.flipit.simulator.actors;

/**
 * Created by jonat on 19/05/2016.
 */
public interface Scheduler {
    void schedule(Actor actor, double time);
    Actor head();
    double headPriority();
}
