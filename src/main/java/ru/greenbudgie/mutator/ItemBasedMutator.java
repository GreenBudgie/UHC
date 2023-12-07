package ru.greenbudgie.mutator;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.UHC;
import ru.greenbudgie.UHC.UHCPlayer;
import ru.greenbudgie.event.GameStartEvent;

import java.util.List;
import java.util.Map;

public abstract class ItemBasedMutator extends Mutator implements Listener {

	public abstract boolean isOnlyPreGame();

	public abstract List<ItemStack> getItemsToAdd();

	@Override
	public void onChoose() {
		if(UHC.state.isBeforeDeathmatch()) {
			List<ItemStack> items = getItemsToAdd();
			for(UHCPlayer uhcPlayer : PlayerManager.getAlivePlayers()) {
				if(uhcPlayer.isOnline()) {
					Player player = uhcPlayer.getPlayer();
					Map<Integer, ItemStack> notEnoughSpace = player.getInventory().addItem(items.toArray(new ItemStack[0]));
					for(ItemStack item : notEnoughSpace.values()) {
						Item drop = player.getWorld().dropItem(player.getLocation(), item);
						drop.setPickupDelay(0);
					}
				} else {
					for(ItemStack item : items) {
						Item drop = uhcPlayer.getLocation().getWorld().dropItem(uhcPlayer.getLocation(), item);
						drop.setPickupDelay(0);
					}
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

	@EventHandler
	public void giveItemsOnGameStart(GameStartEvent event) {
		for(Player player : PlayerManager.getAliveOnlinePlayers()) {
			player.getInventory().addItem(getItemsToAdd().toArray(new ItemStack[0]));
		}
	}

}
