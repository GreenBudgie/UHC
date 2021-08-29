package ru.items;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import ru.util.ItemUtils;

public abstract class ClassCustomItem extends CustomItem {

    public abstract String getDescription();

    @Override
    public boolean isGlowing() {
        return false;
    }

    @Override
    public ItemStack getItemStack() {
        ItemStack item = super.getItemStack();
        ItemUtils.addSplittedLore(item, ChatColor.GOLD + getDescription());
        item.addUnsafeEnchantment(Enchantment.VANISHING_CURSE, 1);
        return item;
    }
}
