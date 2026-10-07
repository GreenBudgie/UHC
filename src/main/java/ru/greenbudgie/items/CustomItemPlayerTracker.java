package ru.greenbudgie.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import ru.greenbudgie.UHC.ActionBarManager;
import ru.greenbudgie.UHC.ClosestEnemyInfoHelper;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.util.item.ItemInfo;

public class CustomItemPlayerTracker extends RequesterCustomItem {

	public String getName() {
		return ChatColor.GREEN + "Player Tracker";
	}

	public Material getMaterial() {
		return Material.ENDER_EYE;
	}

	@Override
	public ItemInfo getDescription() {
		return new ItemInfo("Выводит координаты, дистанцию и направление к ближайшему игроку, когда держишь предмет в руке");
	}

	@Override
	public int getRedstonePrice() {
		return 40;
	}

	@Override
	public int getLapisPrice() {
		return 6;
	}

	@Override
	public void update() {
		if (!ActionBarManager.isUpdateTick()) {
			return;
		}

		for (Player player : PlayerManager.getAliveOnlinePlayers()) {
			if (!isHolding(player)) {
				continue;
			}

			ClosestEnemyInfoHelper.showClosestEnemyInfo(player);
		}
	}

	@Override
	public void onUseRight(Player p, ItemStack item, PlayerInteractEvent e) {
		e.setCancelled(true);
	}

}
