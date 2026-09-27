package ru.greenbudgie.UHC;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class CraftingTweaks implements Listener {

    @EventHandler
    public void onCraft(PrepareItemCraftEvent event) {
        if (event.getRecipe() != null) {
            ItemStack result = event.getRecipe().getResult();
            if (Enchantment.EFFICIENCY.canEnchantItem(result)) {
                result.addEnchantment(Enchantment.EFFICIENCY, 3);
                event.getInventory().setResult(result);
            }
        }
    }

}
