package ru.items;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface BlockHolder {

    void placeBlock(Location location, Player owner);
    boolean canPlaceOnDeathmatch();

}
