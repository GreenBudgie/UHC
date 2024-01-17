package ru.greenbudgie.mutator;

import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import ru.greenbudgie.mutator.base.ItemBasedMutator;
import ru.greenbudgie.mutator.base.ThreatStatus;

import java.util.List;

public class MutatorWizardBattle extends ItemBasedMutator {

	@Override
	public Material getItemToShow() {
		return Material.ENCHANTING_TABLE;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.SUPPORTING;
	}

	@Override
	public String getName() {
		return "Битва Чародеев";
	}

	@Override
	public String getDescription() {
		return "Всем игрокам выдается чарка и стак книжных полок";
	}

	@Override
	public boolean isOnlyPreGame() {
		return false;
	}

	@Override
	public List<ItemStack> getItemsToAdd() {
		return Lists.newArrayList(new ItemStack(Material.ENCHANTING_TABLE), new ItemStack(Material.BOOKSHELF, 64));
	}

}
