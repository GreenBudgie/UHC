package ru.greenbudgie.UHC.configuration;

public enum FastStart {

    DISABLED,
    NO_MUTATORS,
    WITH_MUTATORS;

    public FastStart nextValue() {
        return switch (this) {
            case DISABLED -> NO_MUTATORS;
            case NO_MUTATORS -> WITH_MUTATORS;
            case WITH_MUTATORS -> DISABLED;
        };
    }

}
