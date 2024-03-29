package ru.greenbudgie.mutator.preference;

import ru.greenbudgie.mutator.base.Mutator;
import ru.greenbudgie.util.weighted.WeightedList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeightedMutatorList extends WeightedList<Mutator, WeightedMutator> {

    private static final int WEIGHT_PER_PLAYER = 2;
    private final List<Mutator> initialWeightedMutators;

    public WeightedMutatorList(List<Mutator> weightedMutators) {
        initialWeightedMutators = weightedMutators.stream().toList();
        Map<Mutator, Integer> mutatorCount = new HashMap<>();
        for (Mutator mutator : weightedMutators) {
            mutatorCount.computeIfPresent(mutator, (currentMutator, count) -> count + WEIGHT_PER_PLAYER);
            mutatorCount.putIfAbsent(mutator, 1);
        }
        List<WeightedMutator> weightedList = mutatorCount.entrySet()
                .stream()
                .map(entry -> new WeightedMutator(entry.getKey(), entry.getValue()))
                .toList();
        initialize(weightedList);
    }

    public List<Mutator> getInitialWeightedMutators() {
        return initialWeightedMutators;
    }
}
