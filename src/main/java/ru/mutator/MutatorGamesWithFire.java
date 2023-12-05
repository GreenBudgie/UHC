package ru.mutator;

import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import ru.util.InventoryHelper;

import java.util.List;

public class MutatorGamesWithFire extends ItemBasedMutator {

	@Override
	public Material getItemToShow() {
		return Material.FLINT_AND_STEEL;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.DANGEROUS;
	}

	@Override
	public String getName() {
		return "Игры с Огнем";
	}

	@Override
	public String getDescription() {
		return "Всем игрокам в начале игры выдается огниво";
	}

	@Override
	public boolean isOnlyPreGame() {
		return true;
	}

	@Override
	public List<ItemStack> getItemsToAdd() {
		return Lists.newArrayList(new ItemStack(Material.FLINT_AND_STEEL));
	}

}
