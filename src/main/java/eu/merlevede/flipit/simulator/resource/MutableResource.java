package eu.merlevede.flipit.simulator.resource;

import eu.merlevede.flipit.simulator.actors.Player;

/**
 * Mutable version of {@link ReadableResource}; i.e. a version of the read-only resource whose owner can be changed.
 *
 * Created by jonat on 19/04/2016.
 */
public interface MutableResource extends ReadableResource {
    /**
     * Changes the owner of this resource to be equal to the given player.
     *
     * Some remarks:
     * <ul>
     *  <li>Changes in ownership of the resource are not immediately visible, because {@link #getOwner()} returns the owner of the resource right before the current moment in thime.</li>
     *  <li>If multiple flips occur at the same moment in time, one of the players who flipped will become the new owner.</li>
     *  <li>If the owner was one of the players who flipped at the same moment in time, she will always remain in control of the resource.</li>
     * </ul>
     *
     * @param player The given player who is to be the owner of this resource.
     */
    void setOwner(Player player);

    /**
     * Turns resources into mutable resources.
     * Should only be called on resources contained by this one.
     *
     * @throws IllegalArgumentException If this resource does not contain the given resource.
     * @param resource The resource to make mutable.
     * @return A mutable version of the given resource.
     */
    MutableResource makeMutable(Resource resource);
}
