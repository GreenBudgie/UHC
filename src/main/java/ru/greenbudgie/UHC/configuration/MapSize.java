package ru.greenbudgie.UHC.configuration;

public enum MapSize {

    SMALL(36, false),
    DEFAULT(52, false),
    BIG(68, false),
    FIXED(512, true);

    private final int size;
    private final boolean isFixedSize;

    MapSize(int size, boolean isFixedSize) {
        this.size = size;
        this.isFixedSize = isFixedSize;
    }

    public int getSize() {
        return size;
    }

    public boolean isFixedSize() {
        return isFixedSize;
    }

    public MapSize nextValue() {
        return switch (this) {
            case SMALL -> DEFAULT;
            case DEFAULT -> BIG;
            case BIG -> FIXED;
            case FIXED -> SMALL;
        };
    }

}
