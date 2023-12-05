package ru.items;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import ru.util.ItemInfo;

public abstract class ClassCustomItem extends CustomItem {

    public abstract ItemInfo getDescription();

    @Override
    public ItemStack getItemStack() {
        ItemStack item = super.getItemStack();
        getDescription().applyToItem(item);
        item.addUnsafeEnchantment(Enchantment.VANISHING_CURSE, 1);
        return item;
    }
}
