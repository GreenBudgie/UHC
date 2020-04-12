package ru.mutator;

import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MutatorWizardBattle extends ItemBasedMutator {

	@Override
	public Material getItemToShow() {
		return Material.ENCHANTING_TABLE;
	}

	@Override
	public String getName() {
		return "����� ��������";
	}

	@Override
	public String getDescription() {
		return "���� ������� �������� ����� � ���� ������� �����";
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
