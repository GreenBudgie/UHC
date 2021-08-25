package ru.items;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import ru.util.ItemUtils;

public abstract class ClassCustomItem extends CustomItem {

    public abstract String getDescription();

    @Override
    public ItemStack getItemStack() {
        ItemStack item = super.getItemStack();
        ItemUtils.addSplittedLore(item, ChatColor.GOLD + getDescription());
        return item;
    }
}
