package eu.merlevede.flipit.simulator;

import eu.merlevede.flipit.simulator.actors.Actor;
import eu.merlevede.flipit.simulator.actors.Scheduler;
import eu.merlevede.flipit.simulator.dagger.DaggerGameComponent;
import eu.merlevede.flipit.simulator.dagger.GameComponent;
import eu.merlevede.flipit.simulator.dagger.GameModule;
import eu.merlevede.flipit.simulator.resource.MutableResource;

import javax.inject.Inject;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Class representing a FlipIt game instance.
 *
 * The responsabilities of this class include:
 * <ul>
 *     <li>Grouping actors and resources that belong to the same game.</li>
 *     <li>Keeping track of which actor is the next to move.</li>
 * </ul>
 *
 * Created by jonat on 16/04/2016.
 */
public class Game {
    /**
     * Set of actors that are part of this game.
     */
    private final Set<Actor> actors;

    /**
     * Resource that remembers who's its current owner.
     */
    private final MutableResource mutableResource;

    /**
     * Clock that keeps track of the game's current time.
     */
    private final Clock clock;

    /**
     * Scheduler that keeps track of which actor is next to move.
     */
    private final Scheduler scheduler;

    /**
     * Creates a supplier of game instances using the supplied game configuration.
     * To supply a single Game, a single call is made to each of the factories in the game configuration.
     *
     * @param config The game configuration to use for creating new games.
     * @return Supplier of game instances.
     */
    public static Supplier<Game> supplier(GameConfiguration config) {
        GameModule module = GameModule.create(config);
        GameComponent gc = DaggerGameComponent.builder().gameModule(module).build();
        return () -> gc.gameInstanceComponent().game();
    }

    @Inject
    Game(Set<Actor> actors, MutableResource mutableResource, Clock clock, Scheduler scheduler) {
        //Preconditions.checkArgument(actor.contains(mutableResource.getOwner()), "The owner of the mutableResource has to be an actor in the game");
        this.mutableResource = mutableResource;
        this.actors = actors;
        this.clock = clock;
        this.scheduler = scheduler;
    }

    public Scheduler getScheduler() {
        return this.scheduler;
    }

    public Clock getClock() {
        return this.clock;
    }

    public Set<Actor> getActors() {
        return actors;
    }

    public MutableResource getMutableResource() {
        return mutableResource;
    }

}
