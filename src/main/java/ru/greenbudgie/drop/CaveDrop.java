package ru.greenbudgie.drop;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import ru.greenbudgie.UHC.WorldManager;
import ru.greenbudgie.items.CustomItems;
import ru.greenbudgie.util.MathUtils;
import ru.greenbudgie.util.weighted.WeightedItem;
import ru.greenbudgie.util.weighted.WeightedItemList;

import static org.bukkit.ChatColor.*;

public class CaveDrop extends ChestBasedDrop {

    private static final WeightedItemList FILLERS = new WeightedItemList(
            WeightedItem.builder(Material.STRING).amount(1, 3).weight(3).build(),
            WeightedItem.builder(Material.FLINT).amount(1, 3).weight(3).build(),
            WeightedItem.builder(Material.BREAD).amount(1, 3).weight(3).build(),
            WeightedItem.builder(Material.CARROT).amount(1, 3).weight(3).build(),
            WeightedItem.builder(Material.FEATHER).amount(1, 3).weight(3).build(),
            WeightedItem.builder(Material.LEATHER).weight(3).build(),
            WeightedItem.builder(Material.PAPER).amount(1, 3).weight(3).build(),
            WeightedItem.builder(Material.LAPIS_LAZULI).amount(1, 3).weight(3).build(),
            WeightedItem.builder(Material.REDSTONE).amount(2, 5).weight(3).build(),
            WeightedItem.builder(Material.IRON_INGOT).amount(1, 3).weight(3).build(),
            WeightedItem.builder(Material.GOLD_INGOT).amount(1, 2).weight(3).build(),
            WeightedItem.builder(CustomItems.darkArtifact.getItemStack()).weight(3).build(),
            WeightedItem.builder(Material.APPLE).weight(2).build(),
            WeightedItem.builder(Material.ARROW).amount(1, 2).weight(2).build(),
            WeightedItem.builder(Material.OBSIDIAN).amount(1, 4).weight(2).build(),
            WeightedItem.builder(Material.DIAMOND).weight(1).build()
    );

    @Override
    public String getName() {
        return DARK_GREEN + "" + BOLD + "Кейвдроп";
    }

    @Override
    public int getDefaultDropDelay() {
        return 720;
    }

    @Override
    protected Material getCasing() {
        return Material.RED_STAINED_GLASS;
    }

    @Override
    public WeightedItemList getFillers() {
        return FILLERS;
    }

    @Override
    public Location getRandomLocation() {
        int size = ((int) WorldManager.getActualMapSize()) / 2 - 10;
        int x = MathUtils.randomRange(
                WorldManager.spawnLocation.getBlockX() - size,
                WorldManager.spawnLocation.getBlockX() + size);
        int z = MathUtils.randomRange(
                WorldManager.spawnLocation.getBlockZ() - size,
                WorldManager.spawnLocation.getBlockZ() + size);
        int minHeight = WorldManager.getGameMap().getMinHeight() + 8;
        int y = MathUtils.randomRange(minHeight, 0);
        return new Location(WorldManager.getGameMap(), x, y, z);
    }

    @Override
    protected int getMinFillers() {
        return 3;
    }

    @Override
    protected int getMaxFillers() {
        return 6;
    }

    @Override
    public ChatColor getMarkerColor() {
        return DARK_GREEN;
    }

}
