package ru.mutator;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.UHC.GameState;
import ru.UHC.UHC;
import ru.UHC.WorldManager;
import ru.requester.ItemRequester;
import ru.requester.RequestedItem;
import ru.util.MathUtils;
import ru.util.TaskManager;
import ru.util.WorldHelper;

public class MutatorUnexpectedRequests extends Mutator {

	private int timeToNextRequest = 0;

	@Override
	public Material getItemToShow() {
		return Material.PHANTOM_MEMBRANE;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.SUPPORTING;
	}

	@Override
	public String getName() {
		return "Неожиданные Запросы";
	}

	@Override
	public String getDescription() {
		return "Иногда на карте будут самостоятельно совершаться запросы случайных вещей";
	}

	@Override
	public void onChoose() {
		reset();
	}

	private void reset() {
		timeToNextRequest = MathUtils.randomRange(200, 450);
	}

	private Location getRandomLocation() {
		int size = ((int) WorldManager.getGameMap().getWorldBorder().getSize()) / 2 - 10;
		int x = MathUtils.randomRange(WorldManager.spawnLocation.getBlockX() - size, WorldManager.spawnLocation.getBlockX() + size);
		int z = MathUtils.randomRange(WorldManager.spawnLocation.getBlockZ() - size, WorldManager.spawnLocation.getBlockZ() + size);
		int y = WorldManager.getGameMap().getHighestBlockYAt(x, z);
		return new Location(WorldManager.getGameMap(), x, y, z);
	}

	@Override
	public void update() {
		if(UHC.state.isInGame()) {
			if(TaskManager.isSecUpdated()) {
				if(timeToNextRequest > 0) {
					timeToNextRequest--;
				} else {
					Location l = getRandomLocation();
					for(Player player : UHC.getInGamePlayers()) {
						player.sendMessage(
								ChatColor.LIGHT_PURPLE + "Был сделан запрос: " + ChatColor.DARK_AQUA + l.getBlockX() + ChatColor.WHITE + ", " + ChatColor.DARK_AQUA + l.getBlockZ()
										+ (ChatColor.WHITE + " (" + (l.getWorld() == player.getWorld() ?
										(ChatColor.AQUA + String.valueOf((int) l.distance(player.getLocation()))) :
										WorldHelper.getEnvironmentNamePrepositional(l.getWorld().getEnvironment(), ChatColor.AQUA)) + ChatColor.WHITE + ")"));
					}
					ItemRequester.requestedItems.add(new RequestedItem(l, MathUtils.choose(ItemRequester.requesterCustomItems).getItemStack()));
					reset();
				}
			}
		}
	}

}
