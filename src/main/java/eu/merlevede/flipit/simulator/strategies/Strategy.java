package eu.merlevede.flipit.simulator.strategies;

import eu.merlevede.flipit.simulator.actions.Trigger;
import eu.merlevede.flipit.simulator.actions.TriggerVisitor;
import eu.merlevede.flipit.simulator.actors.Player;

import java.util.OptionalDouble;

/**
 * Class of FlipIt strategies.
 * A strategy can be seen as a "plan of action" or recipe that describes with which probability to perform what actions when.
 * A strategy is immutable and defines how each player acts "on average", from t=0 to t=infinity
 * (together with the other recipes in case of adaptable strategies).
 *
 * In order for the strategy to generate specific actions (i.e. to go from a "plan of action" to "action"),
 * concrete information about the state of the game (e.g. previous actions) is required. {@link T} contains this information.
 *
 * Created by Jonathan on 20/04/2016.
 */
public abstract class Strategy {
    private boolean isInitialized = false;

    /**
     * Gives this strategy the opportunity to act.
     * The given trigger indicates why this strategy is given the opportunity to act, and may contain some additional
     * information that the strategy can use in deciding how to act.
     *
     * Init has to be called before act.
     *
     * @throws IllegalStateException {@link #init()} has to be called before {@link #act(Trigger, Player.Actions)}
     * @param trigger Trigger object, i.e. object that indicates why this strategy is given the opportunity to act.
     * @param actions Actions object, i.e. object that provides this strategy with the means to act
     */
    public final void act(Trigger trigger, Player.Actions actions) {
        if (!isInitialized) { throw new IllegalStateException("Cannot act before initialization"); }
        OptionalDouble d = trigger.accept(getVisitor(actions));
        if (d.isPresent()) {
            actions.schedule(d.getAsDouble());
        }
    }
    abstract TriggerVisitor getVisitor(Player.Actions actions);

    /**
     * Initializes this strategy.
     *
     * Subclasses of strategy should implement this method by overriding {@link #init2()}.
     *
     * @return When to schedule the first call to {@link #act(Trigger, Player.Actions)}.
     */
    public final double init() {
        isInitialized = true;
        return init2();
    }

    /**
     * Hook into init.
     *
     * @see #init()
     */
    protected abstract double init2();
}
