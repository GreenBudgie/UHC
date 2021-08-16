package ru.drop;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import ru.UHC.WorldManager;
import ru.items.CustomItems;
import ru.util.MathUtils;

import java.util.ArrayList;
import java.util.List;

public class NetherDrop extends ChestBasedDrop {

    @Override
    public String getName() {
        return ChatColor.DARK_RED + "" + ChatColor.BOLD + "Незердроп";
    }

    @Override
    public int getDefaultDropDelay() {
        return 1200;
    }

    @Override
    protected Material getCasing() {
        return Material.CRYING_OBSIDIAN;
    }

    @Override
    public List<ItemStack> getFillers() {
        List<ItemStack> fillers = new ArrayList<>();
        fillers.add(new ItemStack(Material.NETHER_WART, MathUtils.randomRange(1, 2)));
        fillers.add(new ItemStack(Material.PORKCHOP, MathUtils.randomRange(2, 3)));
        fillers.add(new ItemStack(Material.GOLDEN_CARROT, MathUtils.randomRange(1, 2)));
        fillers.add(new ItemStack(Material.FERMENTED_SPIDER_EYE, MathUtils.randomRange(1, 2)));
        fillers.add(new ItemStack(Material.BLAZE_POWDER));
        fillers.add(new ItemStack(Material.GOLD_NUGGET, MathUtils.randomRange(5, 14)));
        fillers.add(new ItemStack(Material.MAGMA_CREAM));
        fillers.add(new ItemStack(Material.GUNPOWDER, MathUtils.randomRange(1, 3)));
        fillers.add(new ItemStack(Material.FIRE_CHARGE));
        if(MathUtils.chance(30)) fillers.add(new ItemStack(Material.ANCIENT_DEBRIS));
        return fillers;
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
        int y = MathUtils.randomRange(7, 16);
        return new Location(WorldManager.getGameMapNether(), x, y, z);
    }

    @Override
    public World.Environment getSpawnEnvironment() {
        return World.Environment.NETHER;
    }
}
