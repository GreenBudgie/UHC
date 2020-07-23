package ru.mutator;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import ru.util.InventoryHelper;

public class MutatorBinding extends Mutator implements Listener {

	@Override
	public Material getItemToShow() {
		return Material.BLACK_DYE;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.DANGEROUS;
	}

	@Override
	public String getName() {
		return "��������� �����";
	}

	@Override
	public String getDescription() {
		return "����� ����������� ����� ������������� �������� ��������� �����������. �� ���������������� �� ��� � ������, ������� � �.�.";
	}

	@Override
	public boolean conflictsWith(Mutator another) {
		return another == MutatorManager.diamondLeather;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void craft(PrepareItemCraftEvent e) {
		if(e.getRecipe() != null) {
			ItemStack result = e.getRecipe().getResult();
			if(InventoryHelper.isArmor(result)) {
				result.addEnchantment(Enchantment.BINDING_CURSE, 1);
				e.getInventory().setResult(result);
			}
		}
	}


}
