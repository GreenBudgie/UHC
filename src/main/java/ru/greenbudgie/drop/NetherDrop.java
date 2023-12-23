package ru.greenbudgie.drop;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import ru.greenbudgie.UHC.WorldManager;
import ru.greenbudgie.items.CustomItems;
import ru.greenbudgie.util.MathUtils;
import ru.greenbudgie.util.weighted.WeightedItem;
import ru.greenbudgie.util.weighted.WeightedItemList;

import static org.bukkit.ChatColor.*;

public class NetherDrop extends ChestBasedDrop {

    private static final WeightedItemList FILLERS = new WeightedItemList(
            WeightedItem.builder(Material.NETHER_WART).amount(1, 2).weight(4).build(),
            WeightedItem.builder(Material.PORKCHOP).amount(2, 2).weight(3).build(),
            WeightedItem.builder(Material.GOLDEN_CARROT).amount(1, 2).weight(3).build(),
            WeightedItem.builder(Material.FERMENTED_SPIDER_EYE).amount(1, 2).weight(3).build(),
            WeightedItem.builder(Material.BLAZE_POWDER).weight(3).build(),
            WeightedItem.builder(Material.GOLD_NUGGET).amount(5, 14).weight(3).build(),
            WeightedItem.builder(Material.MAGMA_CREAM).weight(3).build(),
            WeightedItem.builder(Material.GUNPOWDER).amount(1, 3).weight(3).build(),
            WeightedItem.builder(Material.FIRE_CHARGE).weight(3).build(),
            WeightedItem.builder(Material.COAL).amount(3, 6).weight(3).build(),
            WeightedItem.builder(Material.GLOWSTONE).amount(3, 6).weight(3).build(),
            WeightedItem.builder(Material.BROWN_MUSHROOM).amount(2, 4).weight(3).build(),
            WeightedItem.builder(Material.RED_MUSHROOM).amount(2, 4).weight(3).build(),
            WeightedItem.builder(Material.GLASS_BOTTLE).amount(1, 3).weight(3).build(),
            WeightedItem.builder(CustomItems.darkArtifact.getItemStack()).amount(2, 3).weight(2).build(),
            WeightedItem.builder(Material.ANCIENT_DEBRIS).amount(3, 6).weight(1).build()
    );


    @Override
    public String getName() {
        return DARK_RED + "" + BOLD + "Незердроп";
    }

    @Override
    public int getDefaultDropDelay() {
        return 1080;
    }

    @Override
    protected Material getCasing() {
        return Material.TINTED_GLASS;
    }

    @Override
    public WeightedItemList getFillers() {
        return FILLERS;
    }

    @Override
    public Location getRandomLocation() {
        int size = ((int) WorldManager.getGameMapNether().getWorldBorder().getSize()) / 4 - 10;
        Location center = WorldManager.getGameMapNether().getWorldBorder().getCenter();
        int x = MathUtils.randomRange(
                center.getBlockX() - size,
                center.getBlockX() + size);
        int z = MathUtils.randomRange(
                center.getBlockZ() - size,
                center.getBlockZ() + size);
        int y = MathUtils.randomRange(34, 120);
        return new Location(WorldManager.getGameMapNether(), x, y, z);
    }

    @Override
    protected int getMainItemsCount() {
        return 2;
    }

    @Override
    protected int getMinFillers() {
        return 4;
    }

    @Override
    protected int getMaxFillers() {
        return 7;
    }

    @Override
    public World.Environment getSpawnEnvironment() {
        return World.Environment.NETHER;
    }

    @Override
    public ChatColor getMarkerColor() {
        return DARK_RED;
    }

}
