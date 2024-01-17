package ru.greenbudgie.mutator;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import ru.greenbudgie.mutator.base.Mutator;
import ru.greenbudgie.mutator.base.ThreatStatus;

public class MutatorInfiniteBows extends Mutator implements Listener {

	@Override
	public Material getItemToShow() {
		return Material.BOW;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.SUPPORTING;
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
