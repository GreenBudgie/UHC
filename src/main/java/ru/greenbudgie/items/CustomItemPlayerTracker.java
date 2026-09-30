package ru.greenbudgie.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import ru.greenbudgie.UHC.ActionBarManager;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.UHCPlayer;
import ru.greenbudgie.util.LocationFormatter;
import ru.greenbudgie.util.item.ItemInfo;

import static org.bukkit.ChatColor.*;

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

			ActionBarManager.queueMessage(player, 1, () -> getNearestPlayerMessage(player));
		}
	}

	@Override
	public void onUseRight(Player p, ItemStack item, PlayerInteractEvent e) {
		e.setCancelled(true);
	}

	private String getNearestPlayerMessage(Player player) {
		UHCPlayer closestPlayer = null;
		double minDistanceSquared = -1;
		var teammate = PlayerManager.getUHCTeammate(player);
		for (UHCPlayer otherPlayer : PlayerManager.getAlivePlayers()) {
			if (player == otherPlayer.getPlayer() || otherPlayer == teammate) {
				continue;
			}

			if (otherPlayer.getLocation().getWorld() != player.getWorld()) {
				continue;
			}

			var distanceSquared = player.getLocation().distanceSquared(otherPlayer.getLocation());
			if (closestPlayer == null || distanceSquared < minDistanceSquared) {
				closestPlayer = otherPlayer;
				minDistanceSquared = distanceSquared;
			}
		}

		if (closestPlayer == null) {
			return GOLD + "Ближайший игрок не найден";
		}

		var closestPlayerLocation = closestPlayer.getLocation();
		if (closestPlayerLocation == null) {
			return GOLD + "Ближайший игрок не найден";
		}

		String separator = GRAY + " | ";
		String locationInfo = LocationFormatter.formatToWithDistance(
				player.getLocation(),
				closestPlayerLocation,
				DARK_AQUA,
				GRAY,
				AQUA,
				DARK_GRAY,
				false
		);
		String info = GOLD + "Ближайший игрок: " + RED + closestPlayer.getNickname()
				+ RESET + separator +
				locationInfo + AQUA + " " +
				LocationFormatter.getArrowPointingTo(player.getLocation(), closestPlayerLocation);

		return info;
	}
}
