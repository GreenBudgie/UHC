package ru.greenbudgie.mutator.preference;

import ru.greenbudgie.mutator.Mutator;
import ru.greenbudgie.util.weighted.WeightedElement;

import javax.annotation.Nonnull;

public class WeightedMutator extends WeightedElement<Mutator> {

    public WeightedMutator(@Nonnull Mutator element, int weight) {
        super(element, weight);
    }

}
