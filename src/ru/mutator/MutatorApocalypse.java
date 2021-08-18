package ru.mutator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.Listener;
import ru.UHC.PlayerManager;
import ru.UHC.UHC;
import ru.UHC.UHCPlayer;
import ru.UHC.WorldManager;
import ru.util.MathUtils;
import ru.util.TaskManager;

public class MutatorApocalypse extends Mutator implements Listener {

	private int timeToDrop = 0;

	@Override
	public Material getItemToShow() {
		return Material.TNT;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.CRITICAL;
	}

	@Override
	public String getName() {
		return "Апокалипсис";
	}

	@Override
	public String getDescription() {
		return "С неба начинает падать динамит!";
	}

	@Override
	public void onChoose() {
		reset();
	}

	public void reset() {
		timeToDrop = MathUtils.randomRange(20, 35);
	}

	private Location getRandomLocation() {
		UHCPlayer target = MathUtils.choose(PlayerManager.getAlivePlayers());
		int x = target.getLocation().getBlockX() + MathUtils.randomRange(-15, 15);
		int z = target.getLocation().getBlockZ() + MathUtils.randomRange(-15, 15);
		int y = WorldManager.getGameMap().getMaxHeight() - 1;
		return new Location(WorldManager.getGameMap(), x, y, z);
	}

	@Override
	public void update() {
		if(UHC.state.isInGame()) {
			if(TaskManager.isSecUpdated()) {
				timeToDrop--;
				if(timeToDrop <= 0) {
					Location dropLocation = getRandomLocation();
					TNTPrimed tnt = (TNTPrimed) dropLocation.getWorld().spawnEntity(dropLocation, EntityType.PRIMED_TNT);
					tnt.setFuseTicks(12 * 20);
					reset();
				}
			}
		}
	}

}
