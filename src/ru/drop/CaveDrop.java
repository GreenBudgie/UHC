package ru.drop;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import ru.UHC.WorldManager;
import ru.items.CustomItems;
import ru.util.MathUtils;

import java.util.ArrayList;
import java.util.List;

public class CaveDrop extends ChestBasedDrop {

    @Override
    public String getName() {
        return ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Кейвдроп";
    }

    @Override
    public int getDefaultDropDelay() {
        return 960;
    }

    @Override
    protected Material getCasing() {
        return Material.RED_STAINED_GLASS;
    }

    @Override
    public List<ItemStack> getFillers() {
        List<ItemStack> fillers = new ArrayList<>();
        fillers.add(new ItemStack(Material.STRING, MathUtils.randomRange(1, 3)));
        fillers.add(new ItemStack(Material.BREAD, MathUtils.randomRange(1, 3)));
        fillers.add(new ItemStack(Material.CARROT, MathUtils.randomRange(1, 3)));
        fillers.add(new ItemStack(Material.FEATHER, MathUtils.randomRange(1, 3)));
        fillers.add(new ItemStack(Material.LEATHER));
        fillers.add(new ItemStack(Material.LAPIS_LAZULI, MathUtils.randomRange(1, 3)));
        fillers.add(new ItemStack(Material.REDSTONE, MathUtils.randomRange(1, 5)));
        fillers.add(new ItemStack(Material.IRON_INGOT, MathUtils.randomRange(1, 3)));
        fillers.add(new ItemStack(Material.GOLD_INGOT, MathUtils.randomRange(1, 2)));
        fillers.add(CustomItems.darkArtifact.getItemStack());
        if(MathUtils.chance(50)) fillers.add(new ItemStack(Material.APPLE));
        if(MathUtils.chance(30)) fillers.add(new ItemStack(Material.DIAMOND));
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
        return new Location(WorldManager.getGameMap(), x, y, z);
    }

}
