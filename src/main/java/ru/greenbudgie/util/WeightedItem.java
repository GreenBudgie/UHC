package ru.greenbudgie.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.ChatColor.*;

public class WeightedItem {

    private final ItemStack item;
    private final int min;
    private final int max;
    private final int weight;

    public WeightedItem(Material type, int min, int max, int weight) {
        this(new ItemStack(type), min, max, weight);
    }

    public WeightedItem(ItemStack item, int min, int max, int weight) {
        this.item = item;
        this.item.setAmount(min);
        this.min = min;
        this.max = max;
        this.weight = weight;
    }

    public ItemStack getPreviewItem() {
        return ItemUtils.builder(item.clone())
                .ifTrue(min != max).withLore(AQUA + "" + BOLD + min + "-" + max + GRAY + " шт.")
                .ifTrue(min == max).withLore(AQUA + "" + BOLD + min + GRAY + " шт.")
                .withLore(GRAY + "Частота" + DARK_GRAY + ": " + DARK_AQUA + BOLD + weight)
                .build();
    }

    public ItemStack getItem() {
        return ItemUtils.builder(item.clone())
                .withAmount(MathUtils.randomRange(min, max))
                .build();
    }

    public int getWeight() {
        return weight;
    }
}
