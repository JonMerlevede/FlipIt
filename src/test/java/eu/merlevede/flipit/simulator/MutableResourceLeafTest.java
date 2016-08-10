package eu.merlevede.flipit.simulator;

import eu.merlevede.flipit.simulator.actors.Player;
import eu.merlevede.flipit.simulator.dagger.GameInstanceComponent;
import eu.merlevede.flipit.simulator.resource.MutableResourceLeaf;
import org.junit.*;

import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by Jonathan on 21/04/2016.
 */
public class MutableResourceLeafTest {
    GameInstanceComponent gameInstanceComponent;
    Clock clock;
    Player attacker;
    Player defender;
    MutableResourceLeaf oneLeaf;
    MutableResourceLeaf anotherLeaf;

    @Before
    public void before() {
        gameInstanceComponent = TestHelper.getTestComponent();
        attacker = gameInstanceComponent.attacker();
        defender = gameInstanceComponent.defender();
        Random random = new Random();
        clock = gameInstanceComponent.game().getClock();
        oneLeaf = new MutableResourceLeaf(defender, clock, random);
        anotherLeaf = new MutableResourceLeaf(attacker, clock, random);
    }

    @org.junit.Test
    public void getSetOwner() throws Exception {
        oneLeaf.getOwner();
        assertTrue(oneLeaf.getOwner() == defender);
        oneLeaf.setOwner(attacker);
        assertTrue(oneLeaf.getOwner() == defender);
        clock.incrementTime(1);
        assertTrue(oneLeaf.getOwner() == attacker);
        clock.incrementTime(1);
        assertTrue(oneLeaf.getOwner() == attacker);
    }


    @Test
    public void contains() throws Exception {
        assertTrue(oneLeaf.contains(oneLeaf));
        assertFalse(oneLeaf.contains(anotherLeaf));
    }

}