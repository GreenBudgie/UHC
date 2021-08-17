package ru.mutator;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.UHC.PlayerManager;
import ru.UHC.UHC;

import java.util.List;
import java.util.Map;

public abstract class ItemBasedMutator extends Mutator {

	public abstract boolean isOnlyPreGame();

	public abstract List<ItemStack> getItemsToAdd();

	@Override
	public void onChoose() {
		if(UHC.state.isInGame()) {
			List<ItemStack> items = getItemsToAdd();
			for(Player p : PlayerManager.getAliveOnlinePlayers()) {
				Map<Integer, ItemStack> notEnoughSpace = p.getInventory().addItem(items.toArray(new ItemStack[0]));
				for(ItemStack item : notEnoughSpace.values()) {
					Item drop = p.getWorld().dropItem(p.getLocation(), item);
					drop.setPickupDelay(0);
				}
			}
		}
	}

	@Override
	public final boolean canBeAddedFromArtifact() {
		return !isOnlyPreGame();
	}

	@Override
	public final boolean canBeDeactivatedByArtifact() {
		return false;
	}

	@Override
	public final boolean canBeHidden() {
		return false;
	}

}
