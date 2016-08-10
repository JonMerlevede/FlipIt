package eu.merlevede.flipit.br;

import eu.merlevede.flipit.br.hc.HCOptimizer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by jonat on 1/06/2016.
 */
public class HCOptimizerTest {
    private QuadraticState quadraticState;
    @Before
    public void setUp() {
        quadraticState = new QuadraticState(1, -8, 11, 1d/Math.exp(1));
    }

    @Test
    public void solve() throws Exception {
        HCOptimizer.Params params = new HCOptimizer.Params.Builder()
                .setRandom(new Random(1))
                .setStepsizeFunctionSupplier(StepsizeFunctions.constant(0.01d))
                .setMaximumNumberOfStepsWithoutImprovement(OptionalInt.of(50))
                .setMaximizing(false)
                .build();
        HCOptimizer optimizer = HCOptimizer.create(params);
        HCOptimizer.Result<QuadraticState> sol = optimizer.optimize(quadraticState);
//        System.out.println(sol);
        Assert.assertEquals(4d, sol.getState().getPosition(), 0.01);
    }

    @Test
    public void solveDynamic() throws Exception {
        Random r = new Random(1);
        for (int i=0; i < 200; i++) {
            double startVal = (r.nextDouble()-0.5)*20;
            QuadraticState quadraticState = new QuadraticState(1, -8, 11, startVal);
            HCOptimizer.Params params = new HCOptimizer.Params.Builder()
                    .setRandom(new Random(r.nextInt()))
                    .setStepsizeFunctionSupplier(StepsizeFunctions.dynamicExponential(1, 20, 0.001))
                    .setMaximumNumberOfStepsWithoutImprovement(OptionalInt.of(100))
                    .setMaximizing(false)
                    .build();
            HCOptimizer optimizer = HCOptimizer.create(params);
            HCOptimizer.Result<QuadraticState> sol = optimizer.optimize(quadraticState);
//            System.out.println(sol);
            Assert.assertEquals(4d, sol.getState().getPosition(), 0.01);
        }
    }

    private HCOptimizer.Params createParams(boolean parallel) {
        HCOptimizer.Params.Builder paramsB = new HCOptimizer.Params.Builder()
                .setRandom(new Random(1))
                .setStepsizeFunctionSupplier(StepsizeFunctions.dynamicExponential(1, 20, 0.01))
                .setMaximumNumberOfStepsWithoutImprovement(OptionalInt.of(10))
                .setMaximizing(false)
                .setParallel(parallel);
        return paramsB.build();
    }

    private ArrayList<QuadraticState> createQuadraticStates() {
        ArrayList<QuadraticState> quadraticStates = new ArrayList<>(100);
        Random r = new Random(1);
        for (int i =0 ; i < 1000; i++) {
            double startVal = (r.nextDouble()-0.5)*20;
            QuadraticState quadraticState = new QuadraticState(1, -8, 11, startVal);
            quadraticStates.add(i, quadraticState);
        }
        return quadraticStates;
    }

    @Test
    public void testInternalCreateQuadraticStatesDeterminism() {
        ArrayList<QuadraticState> q1 = createQuadraticStates();
        ArrayList<QuadraticState> q2 = createQuadraticStates();
        Assert.assertEquals(q1, q2);
    }

    private Set<HCOptimizer.Result<QuadraticState>> multipleSequential() {
        ArrayList<QuadraticState> quadraticStates = createQuadraticStates();
        HCOptimizer.Params params = createParams(false);
        HCOptimizer optimizer = HCOptimizer.create(params);
        Set<HCOptimizer.Result<QuadraticState>> sol = optimizer.optimizedStream(quadraticStates).collect(Collectors.toSet());
        return sol;
    }

    private Set<HCOptimizer.Result<QuadraticState>> multipleParallel() {
        ArrayList<QuadraticState> quadraticStates = createQuadraticStates();
        HCOptimizer.Params params = createParams(true);
        HCOptimizer optimizer = HCOptimizer.create(params);
        Set<HCOptimizer.Result<QuadraticState>> sol = optimizer.optimizedStream(quadraticStates).collect(Collectors.toSet());
        return sol;
    }

    @Test
    public void testDeterminism() throws Exception {
//        Set<HCOptimizer.GameResult<QuadraticState>> sequential = Timer.time("sequential", this::multipleSequential);
//        Set<HCOptimizer.GameResult<QuadraticState>> parallel = Timer.time("parallel", this::multipleParallel);
        Set<HCOptimizer.Result<QuadraticState>> sequential = multipleSequential();
        Set<HCOptimizer.Result<QuadraticState>> parallel = multipleParallel();
        Assert.assertEquals(sequential, parallel);
    }
}