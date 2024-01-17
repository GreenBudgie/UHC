package ru.greenbudgie.mutator;

import org.bukkit.Location;
import org.bukkit.Material;
import ru.greenbudgie.UHC.UHC;
import ru.greenbudgie.UHC.WorldManager;
import ru.greenbudgie.mutator.base.Mutator;
import ru.greenbudgie.mutator.base.ThreatStatus;
import ru.greenbudgie.requester.ItemRequester;
import ru.greenbudgie.requester.RequestedItem;
import ru.greenbudgie.util.MathUtils;
import ru.greenbudgie.util.TaskManager;

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
		if(UHC.state.isBeforeDeathmatch()) {
			if(TaskManager.isSecUpdated()) {
				if(timeToNextRequest > 0) {
					timeToNextRequest--;
				} else {
					Location l = getRandomLocation();
					RequestedItem requestedItem = new RequestedItem(l, MathUtils.choose(ItemRequester.requesterCustomItems.values()).getItemStack());
					requestedItem.announce(null);
					ItemRequester.requestedItems.add(requestedItem);
					reset();
				}
			}
		}
	}

}
