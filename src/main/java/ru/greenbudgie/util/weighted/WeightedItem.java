package ru.greenbudgie.util.weighted;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import ru.greenbudgie.util.MathUtils;
import ru.greenbudgie.util.item.ItemUtils;

import javax.annotation.Nonnull;

import static org.bukkit.ChatColor.*;

public class WeightedItem extends WeightedElement<ItemStack> {

    private final int min;
    private final int max;

    WeightedItem(ItemStack item, int min, int max, int weight) {
        super(item, weight);
        if (min < 1 || max < 1) {
            throw new IllegalArgumentException("Minimum and maximum amount should not be less than 1");
        }
        int maxStackSize = item.getType().getMaxStackSize();
        if (min > maxStackSize || max > maxStackSize) {
            throw new IllegalArgumentException(
                    "Minimum and maximum amount should not be larger than max stack size of " + maxStackSize
            );
        }
        this.min = min;
        this.max = max;
    }

    public ItemStack getPreviewItem() {
        return ItemUtils.builder(element.clone())
                .withAmount(min)
                .withLore(GRAY + "Шанс " + AQUA + getFormattedChancePercent())
                .ifTrue(min != max).withLore(AQUA + "" + BOLD + min + "-" + max + GRAY + " шт.")
                .build();
    }

    public ItemStack getItem() {
        return ItemUtils.builder(element.clone())
                .withAmount(MathUtils.randomRange(min, max))
                .build();
    }

    public static Builder builder(Material type) {
        return new Builder(type);
    }

    public static Builder builder(ItemStack item) {
        return new Builder(item);
    }

    public static class Builder {

        private final ItemStack item;
        private int min = 1;
        private int max = 1;
        private int weight = 1;

        public Builder(@Nonnull Material type) {
            this(new ItemStack(type));
        }

        public Builder(@Nonnull ItemStack item) {
            this.item = item;
        }

        public Builder amount(int amount) {
            amount(amount, amount);
            return this;
        }

        public Builder amount(int min, int max) {
            this.min = min;
            this.max = max;
            return this;
        }

        public Builder weight(int weight) {
            this.weight = weight;
            return this;
        }

        public WeightedItem build() {
            return new WeightedItem(item, min, max, weight);
        }

    }

}
