package ru.greenbudgie.UHC.configuration;

import javax.annotation.Nonnull;

public class EnumCycler {

    @Nonnull
    public static <T extends Enum<T>> T nextValue(T value, T[] values) {
        if (values.length == 0) {
            throw new IllegalArgumentException("Enum is empty");
        }
        int newOrdinal = value.ordinal() + 1;
        if (newOrdinal >= values.length) {
            return values[0];
        }
        return values[newOrdinal];
    }

}
