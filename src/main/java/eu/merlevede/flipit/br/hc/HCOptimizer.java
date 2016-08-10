package eu.merlevede.flipit.br.hc;

import eu.merlevede.flipit.br.BufferedState;
import eu.merlevede.flipit.br.State;
import eu.merlevede.flipit.br.StepsizeFunction;
import org.inferred.freebuilder.FreeBuilder;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Hill-climbing optimizer
 *
 * Created by jonat on 8/06/2016.
 */
public class HCOptimizer {
    @FreeBuilder
    public interface Params {
        OptionalInt getMaxNumberOfSteps();
        OptionalInt getMaximumNumberOfStepsWithoutImprovement();
        OptionalDouble getMaximumValue();
        OptionalDouble getMinimumValue();
        Supplier<StepsizeFunction> getStepsizeFunctionSupplier();
        Random getRandom();
        boolean isBuffered();
        boolean isMaximizing();
        boolean isParallel();
        class Builder extends HCOptimizer_Params_Builder {
            public Builder() {
                setMaxNumberOfSteps(OptionalInt.empty());
                setMaximumNumberOfStepsWithoutImprovement(OptionalInt.empty());
                setMaximumValue(OptionalDouble.empty());
                setMinimumValue(OptionalDouble.empty());
                setBuffered(true);
                setMaximizing(true);
                setParallel(false);
            }
        }
    }
    @FreeBuilder
    public interface Result<T extends State<T>> {
        int getNumberOfSteps();
        double getStepSize();
        T getState();
        class Builder<S extends State<S>> extends HCOptimizer_Result_Builder<S> {}
    }

    @FreeBuilder
    interface StepParams<T extends State<T>> extends StepsizeFunction.Params {
        T getState();
        StepsizeFunction getStepsizeFunction();
        Random getRandom();
        int getNumberOfStepsWithoutImprovement();
        class Builder<S extends State<S>> extends HCOptimizer_StepParams_Builder<S> {
            public Builder() {
                setCurrentStep(0);
                setNumberOfStepsWithoutImprovement(0);
            }
        }
    }

    private final Params params;

    private HCOptimizer(Params params) {
        this.params = params;
    }

    public static HCOptimizer create(Params params) {
        return new HCOptimizer(params);
    }

    /**
     * Negative energy is improvement.
     */
    private boolean isImprovement(State newState, State oldState) {
        if (params.isMaximizing()) {
            return newState.calculateValue() > oldState.calculateValue();
        } else {
            return oldState.calculateValue() > newState.calculateValue();
        }
    }

    private <T extends State<T>> boolean step(StepParams.Builder<T> stepParams) {
        if (stepParams.getCurrentStep() >= stepParams.getMaxSteps()) {
            return false;
        }
        if (stepParams.getNumberOfStepsWithoutImprovement() >= params.getMaximumNumberOfStepsWithoutImprovement().orElse(Integer.MAX_VALUE)
                && stepParams.getStepsizeFunction().mayStop()) {
            return false;
        }
        if (stepParams.getState().calculateValue() >= params.getMaximumValue().orElse(Double.NaN)) {
            return false;
        }
        if (stepParams.getState().calculateValue() < params.getMinimumValue().orElse(Double.NaN)) {
            return false;
        }
        double stepSize = stepParams.getStepsizeFunction().stepSize(stepParams.build());
        T candidate = stepParams.getState().randomNeighbour(stepParams.getRandom(), stepSize);
        boolean accepted = isImprovement(candidate, stepParams.getState());
        if (accepted) {
            stepParams.setState(candidate);
        }
        if (accepted) {
            stepParams.setNumberOfStepsWithoutImprovement(0);
        } else {
            stepParams.mapNumberOfStepsWithoutImprovement((i) -> i+1);
        }
        stepParams.mapCurrentStep((i) -> i+1);
        return true;
    }

    private static class RandomPlusObj<T> {
        final Random random;
        final T obj;
        public RandomPlusObj(Random random, T obj) {
            this.random = random;
            this.obj = obj;
        }
    }

    public <T extends State<T>> Stream<Result<T>> optimizedStream(List<T> states) {
        Stream<RandomPlusObj<T>> randomStatesStream = states.stream().map(state -> new RandomPlusObj<>(new Random(params.getRandom().nextInt()), state));
        Iterator<RandomPlusObj<T>> ite = randomStatesStream.iterator();
        Stream<Result<T>> str = StreamSupport.stream(Spliterators.spliterator(ite, states.size(), Spliterator.IMMUTABLE), false)
                .map(randomState -> optimize(randomState.obj, randomState.random));
        if (params.isParallel()) {
            str = str.parallel();
        }
        return str;
    }

    public <T extends State<T>> Result<T> optimize(List<T> states) {
        Stream<Result<T>> str = optimizedStream(states);
        Comparator<Result<T>> comparator = (el1,el2) -> Double.compare(el1.getState().calculateValue(), el2.getState().calculateValue());
        Result<T> result;
        if (params.isMaximizing()) {
            result = str.max(comparator).orElseThrow(IllegalArgumentException::new);
        } else {
            result = str.min(comparator).orElseThrow(IllegalArgumentException::new);
        }
        return result;
    }

    public <T extends State<T>> Result<T> optimize(T initialState) {
        return optimize(initialState, new Random(params.getRandom().nextInt()));
    }

    public <T extends State<T>> Result<T> optimize(T initialState, Random random) {
        if (params.isBuffered()) {
            initialState = BufferedState.buffer(initialState);
        }
        StepParams.Builder<T> b = new StepParams.Builder<T>()
                .setMaxSteps(params.getMaxNumberOfSteps().orElse(Integer.MAX_VALUE))
                .setRandom(random)
                .setStepsizeFunction(params.getStepsizeFunctionSupplier().get())
                .setState(initialState);
        while (step(b)) {}
        return new Result.Builder<T>()
                .setNumberOfSteps(b.getCurrentStep())
                .setState(b.getState())
                .setStepSize(b.getStepsizeFunction().stepSize(b.build()))
                .build();
    }

}
