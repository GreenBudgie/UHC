package ru.greenbudgie.util.weighted;

import org.bukkit.enchantments.Enchantment;

public class WeightedEnchantmentList extends WeightedList<Enchantment, WeightedEnchantment> {

    public WeightedEnchantmentList(WeightedEnchantment... enchantments) {
        super(enchantments);
    }

}
