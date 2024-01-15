package ru.greenbudgie.util.weighted;

import javax.annotation.Nonnull;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public abstract class WeightedElement<T> {

    @Nonnull
    protected final T element;
    protected final int weight;

    protected double chance;

    public WeightedElement(@Nonnull T element, int weight) {
        if (weight <= 0) {
            throw new IllegalArgumentException("Weight should not be less than 1");
        }
        this.element = element;
        this.weight = weight;
    }

    @Nonnull
    public T getElement() {
        return element;
    }

    public int getWeight() {
        return weight;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

    public double getChance() {
        return chance;
    }

    public String getFormattedChancePercent() {
        DecimalFormat format = new DecimalFormat("0.0");
        format.setRoundingMode(RoundingMode.HALF_UP);
        double percent = chance * 100.0;
        return format.format(percent) + "%";
    }
}
