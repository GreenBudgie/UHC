package ru.mutator;

import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import ru.util.InventoryHelper;

import java.util.List;

public class MutatorElytra extends ItemBasedMutator {

	@Override
	public Material getItemToShow() {
		return Material.ELYTRA;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.SUPPORTING;
	}

	@Override
	public String getName() {
		return "Летающие Дебилы";
	}

	@Override
	public String getDescription() {
		return "Всем игрокам выдаются элитры";
	}

	@Override
	public boolean isOnlyPreGame() {
		return false;
	}

	@Override
	public List<ItemStack> getItemsToAdd() {
		return Lists.newArrayList(InventoryHelper.setUnbreakable(new ItemStack(Material.ELYTRA)));
	}

}
