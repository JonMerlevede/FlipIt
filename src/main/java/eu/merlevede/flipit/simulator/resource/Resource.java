package eu.merlevede.flipit.simulator.resource;

/**
 * Represents a resource in the FlipIt game.
 *
 * Created by Jonathan on 10/05/2016.
 */
public interface Resource {
    /**
     * Whether or not this resource contains the given resource. Resources contain themselves.
     * @param resource The given resource.
     * @return True if this resource contains the given one, false otherwise.
     */
    boolean contains(Resource resource);
}
