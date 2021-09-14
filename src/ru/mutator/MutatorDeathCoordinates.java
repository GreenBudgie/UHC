package ru.mutator;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.UHC.PlayerManager;
import ru.UHC.UHC;
import ru.UHC.UHCPlayer;
import ru.event.UHCPlayerDeathEvent;
import ru.util.WorldHelper;

public class MutatorDeathCoordinates extends Mutator implements Listener {

	@Override
	public Material getItemToShow() {
		return Material.CRIMSON_SIGN;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.INNOCENT;
	}

	@Override
	public String getName() {
		return "У всех на виду";
	}

	@Override
	public String getDescription() {
		return "При смерти игрока в чат выводятся его координаты";
	}

	private Block getSignBlockBelow(Location location) {
		if(!location.getBlock().getType().isAir()) return null;
		for(int i = 0;; i++) {
			Location currentLocation = location.clone().add(0, -i, 0);
			if(currentLocation.getBlockY() <= 0) return null;
			Block currentBlock = currentLocation.getBlock();
			if(!currentBlock.getType().isAir()) {
				Block blockAbove = location.clone().add(0, -i + 1, 0).getBlock();
				if(blockAbove.getType().isAir() && currentBlock.getType().isSolid()) {
					return blockAbove;
				} else {
					return null;
				}
			}
		}
	}

	@EventHandler
	public void handlePlayerDeath(UHCPlayerDeathEvent event) {
		UHCPlayer uhcPlayer = event.getUHCPlayer();
		Location location = uhcPlayer.getLocation();
		if(UHC.state.isInGame() && location != null) {
			Block signBlock = getSignBlockBelow(location);
			if(signBlock != null) {
				signBlock.setType(Material.CRIMSON_SIGN);
				Sign sign = (Sign) signBlock.getState();
				sign.setLine(0, ChatColor.WHITE + "Трагически");
				sign.setLine(1, ChatColor.WHITE + "погиб");
				sign.setLine(2, ChatColor.WHITE + uhcPlayer.getNickname());
				sign.setLine(3, ChatColor.WHITE + "" + ChatColor.BOLD + "RIP");
				sign.update(true, false);
			}
			String dimension =
					ChatColor.GRAY + " (" +
					WorldHelper.getEnvironmentNamePrepositional(location.getWorld().getEnvironment(), ChatColor.WHITE) +
					ChatColor.GRAY + ")";
			String locationInfo =
					ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "> " +
					ChatColor.GRAY + "Координаты " +
					ChatColor.AQUA + uhcPlayer.getNickname() +
					ChatColor.GRAY + ": " +
					ChatColor.WHITE + location.getBlockX() +
					ChatColor.GRAY + ", " + ChatColor.WHITE + location.getBlockY() +
					ChatColor.GRAY + ", " + ChatColor.WHITE + location.getBlockZ();
			for(Player player : PlayerManager.getInGamePlayersAndSpectators()) {
				if(player.getWorld() != location.getWorld()) {
					player.sendMessage(locationInfo + dimension);
				} else {
					player.sendMessage(locationInfo);
				}
			}
		}
	}

	@Override
	public boolean canBeHidden() {
		return false;
	}
}
