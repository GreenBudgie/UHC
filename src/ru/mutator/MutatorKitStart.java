package ru.mutator;

import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MutatorKitStart extends ItemBasedMutator {

	@Override
	public Material getItemToShow() {
		return Material.CHEST;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.SUPPORTING;
	}

	@Override
	public String getName() {
		return "Стартовый Набор";
	}

	@Override
	public String getDescription() {
		return "Всем игрокам выдается одинаковый стартовый набор брони и инструментов";
	}

	@Override
	public boolean isOnlyPreGame() {
		return true;
	}

	@Override
	public List<ItemStack> getItemsToAdd() {
		return Lists.newArrayList(new ItemStack(Material.STONE_PICKAXE), new ItemStack(Material.STONE_SWORD), new ItemStack(Material.STONE_AXE),
				new ItemStack(Material.STONE_SHOVEL), new ItemStack(Material.CHAINMAIL_HELMET), new ItemStack(Material.CHAINMAIL_BOOTS),
				new ItemStack(Material.OAK_PLANKS, 32), new ItemStack(Material.COOKED_BEEF, 4));
	}

}
