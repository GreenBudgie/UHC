package ru.greenbudgie.mutator;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import ru.greenbudgie.mutator.base.Mutator;
import ru.greenbudgie.mutator.base.ThreatStatus;
import ru.greenbudgie.mutator.manager.MutatorManager;

public class MutatorDiamondLeather extends Mutator implements Listener {

	@Override
	public Material getItemToShow() {
		return Material.LEATHER;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.SUPPORTING;
	}

	@Override
	public String getName() {
		return "Кожаная Алмазка";
	}

	@Override
	public String getDescription() {
		return "Из кожи крафтится алмазная броня";
	}

	@Override
	public boolean conflictsWith(Mutator another) {
		return another == MutatorManager.binding;
	}

	@EventHandler
	public void craft(PrepareItemCraftEvent e) {
		if(e.getRecipe() != null) {
			ItemStack result = e.getRecipe().getResult();
			switch(result.getType()) {
				case LEATHER_HELMET -> e.getInventory().setResult(new ItemStack(Material.DIAMOND_HELMET));
				case LEATHER_CHESTPLATE -> e.getInventory().setResult(new ItemStack(Material.DIAMOND_CHESTPLATE));
				case LEATHER_LEGGINGS -> e.getInventory().setResult(new ItemStack(Material.DIAMOND_LEGGINGS));
				case LEATHER_BOOTS -> e.getInventory().setResult(new ItemStack(Material.DIAMOND_BOOTS));
			}
		}
	}

}
