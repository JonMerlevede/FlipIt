package eu.merlevede.flipit.simulator.strategies;

import eu.merlevede.flipit.simulator.actions.Trigger;
import eu.merlevede.flipit.simulator.actions.TriggerVisitor;
import eu.merlevede.flipit.simulator.actors.Player;
import eu.merlevede.flipit.simulator.strategies.generators.Generator;

import java.util.OptionalDouble;

/**
 * Class of nonadaptive strategies.
 *
 * Created by Jonathan on 10/05/2016.
 */
public class NAStrategy extends Strategy {
    private final Generator generator;
    public NAStrategy(Generator generator) {
        this.generator = generator;
    }
    private final class ReusableTriggerVistior implements TriggerVisitor {
        private Player.Actions actions;
        void setActions(Player.Actions actions) {
            this.actions = actions;
        }
        @Override
        public double scheduled() {
            actions.flip();
            return generator.getAndNext();
        }
    }
    private final ReusableTriggerVistior visitor = new ReusableTriggerVistior();

    @Override
    public TriggerVisitor getVisitor(Player.Actions actions) {
        visitor.setActions(actions);
        return visitor;
    }

    @Override
    public final double init2() {
        return generator.getAndNext();
    }
}
