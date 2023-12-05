package ru.greenbudgie.items;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface BlockHolder {

    /**
     * @return Whether the block is successfully placed
     */
    boolean placeBlock(Location location, Player owner);
    boolean canPlaceOnDeathmatch();

}
