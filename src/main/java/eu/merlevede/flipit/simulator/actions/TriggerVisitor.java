package eu.merlevede.flipit.simulator.actions;

import java.util.OptionalDouble;

/**
 * Implementation of the visitor pattern. Allows to distinguish between different kinds of triggers.
 *
 * The only mandatory trigger to respond to
 *
 * @see Trigger
 *
 * Created by Jonathan on 10/05/2016.
 */
public interface TriggerVisitor {
    double scheduled();
    default OptionalDouble visit(Trigger.Leak leak) { return OptionalDouble.empty(); }
}
