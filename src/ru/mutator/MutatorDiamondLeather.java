package ru.mutator;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class MutatorDiamondLeather extends Mutator implements Listener {

	@Override
	public Material getItemToShow() {
		return Material.LEATHER;
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
			case LEATHER_HELMET:
				e.getInventory().setResult(new ItemStack(Material.DIAMOND_HELMET));
				break;
			case LEATHER_CHESTPLATE:
				e.getInventory().setResult(new ItemStack(Material.DIAMOND_CHESTPLATE));
				break;
			case LEATHER_LEGGINGS:
				e.getInventory().setResult(new ItemStack(Material.DIAMOND_LEGGINGS));
				break;
			case LEATHER_BOOTS:
				e.getInventory().setResult(new ItemStack(Material.DIAMOND_BOOTS));
				break;
			}
		}
	}

}
