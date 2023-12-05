package ru.greenbudgie.mutator;

import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MutatorChemistBattle extends ItemBasedMutator {

	@Override
	public Material getItemToShow() {
		return Material.BREWING_STAND;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.SUPPORTING;
	}

	@Override
	public String getName() {
		return "Битва Алхимиков";
	}

	@Override
	public String getDescription() {
		return "Всем игрокам выдается зельеварка, порошок ифрита и адский нарост";
	}

	@Override
	public boolean isOnlyPreGame() {
		return false;
	}

	@Override
	public List<ItemStack> getItemsToAdd() {
		return Lists.newArrayList(new ItemStack(Material.BREWING_STAND), new ItemStack(Material.BLAZE_POWDER, 1), new ItemStack(Material.NETHER_WART, 8));
	}
}
