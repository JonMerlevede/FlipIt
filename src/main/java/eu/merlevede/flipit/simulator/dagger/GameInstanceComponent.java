package eu.merlevede.flipit.simulator.dagger;

import dagger.Subcomponent;
import eu.merlevede.flipit.simulator.Game;
import eu.merlevede.flipit.simulator.actors.Player;

/**
 * Created by Jonathan on 4/05/2016.
 */
@Subcomponent(modules = GameInstanceModule.class)
@GameInstanceScope
public interface GameInstanceComponent {
    Game game();
    @ForAttacker Player attacker();
    @ForDefender Player defender();
}
