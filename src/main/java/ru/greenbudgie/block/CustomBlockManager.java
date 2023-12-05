package ru.greenbudgie.block;

import java.util.ArrayList;
import java.util.List;

public class CustomBlockManager {

    private static final List<CustomBlock> customBlocks = new ArrayList<>();

    /**
     * Gets the list of custom blocks currently present in the world
     */
    public static List<CustomBlock> getCustomBlocks() {
        return customBlocks;
    }

    /**
     * Removes every registry and real block from the world.
     */
    public static void removeAllBlocks() {
        for(CustomBlock block : customBlocks) {
            block.remove();
        }
    }

    public static void updateBlocks() {
        for(CustomBlock block : customBlocks) {
            block.update();
        }
        customBlocks.removeIf(block -> block.doRemove);
    }

}
