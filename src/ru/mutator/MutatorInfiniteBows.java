package ru.mutator;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class MutatorInfiniteBows extends Mutator implements Listener {

	@Override
	public Material getItemToShow() {
		return Material.BOW;
	}

	@Override
	public String getName() {
		return "Лук Бога";
	}

	@Override
	public String getDescription() {
		return "Любой скрафченный лук получает чар на Бесконечность";
	}

	@EventHandler
	public void craft(PrepareItemCraftEvent e) {
		if(e.getRecipe() != null) {
			ItemStack result = e.getRecipe().getResult();
			if(result.getType() == Material.BOW) {
				result.addEnchantment(Enchantment.ARROW_INFINITE, 1);
				e.getInventory().setResult(result);
			}
		}
	}


}
