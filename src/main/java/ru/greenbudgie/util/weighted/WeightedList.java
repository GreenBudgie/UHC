package ru.greenbudgie.util.weighted;

import ru.greenbudgie.util.MathUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Defines a list that holds elements with weight. It is possible to get random element from this list considering
 * its weight. Higher weight is higher chance.
 */
public abstract class WeightedList<T, E extends WeightedElement<T>> {

    protected final List<E> elements;
    protected final List<E> weightedElements;

    @SafeVarargs
    public WeightedList(E... elements) {
        Comparator<E> weightComparator = Comparator.comparingInt(E::getWeight).reversed();
        this.elements = Arrays.stream(elements).sorted(weightComparator).toList();
        List<E> weightedList = new ArrayList<>();
        for (E element : elements) {
            for (int i = 0; i < element.getWeight(); i++) {
                weightedList.add(element);
            }
        }
        weightedElements = weightedList;
        int size = weightedElements.size();
        for (E element : this.elements) {
            element.setChance(element.weight / (double) size);
        }
    }

    /**
     * Gets all elements in this list, not weighted
     */
    public List<E> getElements() {
        return elements;
    }

    /**
     * Gets the random element from the list considering its weight.
     * Elements with higher weight will be returned more frequently.
     */
    public E getRandomElementWeighted() {
        return MathUtils.choose(weightedElements);
    }

    /**
     * Gets n random elements from the list.
     * Elements with higher weight will be returned more frequently.
     *
     * @param repeat Whether it is possible for elements to repeat in the collection
     */
    public List<E> getRandomElementsWeighted(int n, boolean repeat) {
        if (n == 0) {
            return new ArrayList<>();
        }
        if (!repeat && n > elements.size()) {
            throw new IllegalArgumentException(
                    "Number of queried elements " + n + " is larger than overall collection size " + elements.size()
            );
        }
        List<E> elementsCopy = new ArrayList<>(weightedElements);
        List<E> result = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            E element = MathUtils.choose(elementsCopy);
            if (!repeat) {
                elementsCopy.removeAll(List.of(element));
            }
            result.add(element);
        }
        return result;
    }

    /**
     * Gets n random elements from the list with no repeats.
     * Elements with higher weight will be returned more frequently.
     */
    public List<E> getRandomElementsWeighted(int n) {
        return getRandomElementsWeighted(n, false);
    }

}
