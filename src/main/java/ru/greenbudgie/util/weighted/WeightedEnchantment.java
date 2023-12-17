package ru.greenbudgie.util.weighted;

import org.bukkit.enchantments.Enchantment;
import ru.greenbudgie.util.MathUtils;
import ru.greenbudgie.util.item.Enchant;
import ru.greenbudgie.util.item.EnchantmentLocalizer;

import javax.annotation.Nonnull;

import static org.bukkit.ChatColor.*;

public class WeightedEnchantment extends WeightedElement<Enchantment> {

    private final int minLevel;
    private final int maxLevel;

    public WeightedEnchantment(@Nonnull Enchantment enchantment, int minLevel, int maxLevel, int weight) {
        super(enchantment, weight);
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
    }

    public Enchant getEnchantment() {
        return new Enchant(element, MathUtils.randomRange(minLevel, maxLevel));
    }

    public String getPreviewString(boolean isWeighted) {
        String level;
        if (minLevel == maxLevel) {
            level = EnchantmentLocalizer.localizeLevel(minLevel);
        } else {
            level = EnchantmentLocalizer.localizeLevel(minLevel) + "-" + EnchantmentLocalizer.localizeLevel(maxLevel);
        }
        String enchantment = EnchantmentLocalizer.localizeName(element);
        String enchantmentPreview = GRAY + enchantment + " " + level;
        if (!isWeighted) {
            return enchantmentPreview;
        }
        return enchantmentPreview + WHITE + ", " + GRAY + "шанс " + AQUA + getFormattedChancePercent();
    }

    public static Builder builder(Enchantment enchantment) {
        return new Builder(enchantment);
    }

    public static class Builder {

        @Nonnull
        private final Enchantment enchantment;
        private int minLevel = 1;
        private int maxLevel = 1;
        private int weight = 1;

        public Builder(@Nonnull Enchantment enchantment) {
            this.enchantment = enchantment;
        }

        public Builder level(int level) {
            level(level, level);
            return this;
        }

        public Builder level(int minLevel, int maxLevel) {
            this.minLevel = minLevel;
            this.maxLevel = maxLevel;
            return this;
        }

        public Builder weight(int weight) {
            this.weight = weight;
            return this;
        }

        public WeightedEnchantment build() {
            return new WeightedEnchantment(enchantment, minLevel, maxLevel, weight);
        }

    }

}
