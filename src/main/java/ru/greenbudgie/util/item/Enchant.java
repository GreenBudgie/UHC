package ru.greenbudgie.util.item;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

/**
 * An immutable record storing enchantment and its level.
 * Can store enchantment levels larger than the maximum level.
 */
public class Enchant {

    @Nonnull
    private final Enchantment enchantment;
    private final int level;

    public Enchant(@Nonnull Enchantment enchantment, int level) {
        if (level < 1) {
            throw new IllegalArgumentException("Enchantment level cannot be less than 1");
        }
        this.enchantment = enchantment;
        this.level = level;
    }

    public Enchant(@Nonnull Enchantment enchantment) {
        this(enchantment, 1);
    }

    public ItemStack enchant(ItemStack item) {
        item.addUnsafeEnchantment(enchantment, level);
        return item;
    }

    @Nonnull
    public Enchantment getEnchantment() {
        return enchantment;
    }

    public int getLevel() {
        return level;
    }

}
