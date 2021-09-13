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

	@EventHandler
	public void handlePlayerDeath(UHCPlayerDeathEvent event) {
		UHCPlayer uhcPlayer = event.getUHCPlayer();
		Location location = uhcPlayer.getLocation();
		if(location != null) {
			if(UHC.state.isInGame()) {
				Block highestBlock = location.getWorld().getHighestBlockAt(location);
				if(highestBlock.getType().isSolid()) {
					Block blockAbove = highestBlock.getLocation().clone().add(0, 1, 0).getBlock();
					blockAbove.setType(Material.CRIMSON_SIGN);
					Sign sign = (Sign) blockAbove.getState();
					sign.setLine(0, ChatColor.WHITE + "Трагически");
					sign.setLine(1, ChatColor.WHITE + "погиб");
					sign.setLine(2, ChatColor.WHITE + uhcPlayer.getNickname());
					sign.setLine(3, ChatColor.WHITE + "" + ChatColor.BOLD + "RIP");
					sign.update(true, false);
				}
			}
			String locationInfo =
					ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "> " +
					ChatColor.GRAY + "Координаты " +
					ChatColor.AQUA + uhcPlayer.getNickname() +
					ChatColor.GRAY + ": " +
					ChatColor.WHITE + location.getBlockX() +
					ChatColor.GRAY + ", " + ChatColor.WHITE + location.getBlockY() +
					ChatColor.GRAY + ", " + ChatColor.WHITE + location.getBlockZ();
			for(Player player : PlayerManager.getInGamePlayersAndSpectators()) {
				player.sendMessage(locationInfo);
			}
		}
	}

	@Override
	public boolean canBeHidden() {
		return false;
	}
}
