package ru.greenbudgie.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import ru.greenbudgie.util.item.ItemInfo;

public class CustomItemWindCharge extends RequesterCustomItem implements Listener {

    @Override
    public String getName() {
        return ChatColor.AQUA + "Wind Charge";
    }

    @Override
    public Material getMaterial() {
        return Material.WIND_CHARGE;
    }

    @Override
    public ItemInfo getDescription() {
        return new ItemInfo("Обычный заряд ветра");
    }

    @Override
    public int getRedstonePrice() {
        return 28;
    }

    @Override
    public int getLapisPrice() {
        return 0;
    }

    @Override
    public ItemStack getItemStack() {
        ItemStack item = super.getItemStack();
        item.setAmount(16);
        return item;
    }

}
