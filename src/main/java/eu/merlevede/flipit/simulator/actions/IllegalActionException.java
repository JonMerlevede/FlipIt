package eu.merlevede.flipit.simulator.actions;

/**
 * Exception that indicates that a player cannot execute a certain action (at this time).
 *
 * Created by Jonathan on 2/05/2016.
 */
public class IllegalActionException extends RuntimeException {
    public IllegalActionException() {
        super();
    }

    public IllegalActionException(String message) {
        super(message);
    }
}
