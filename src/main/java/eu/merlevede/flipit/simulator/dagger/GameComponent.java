package eu.merlevede.flipit.simulator.dagger;

import dagger.Component;

/**
 * Created by Jonathan on 4/05/2016.
 */
@Component(modules = GameModule.class)
@GameScope
public interface GameComponent {
    GameInstanceComponent gameInstanceComponent();
}
