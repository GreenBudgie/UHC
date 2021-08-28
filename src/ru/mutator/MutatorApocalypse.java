package ru.mutator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.Listener;
import ru.UHC.*;
import ru.util.MathUtils;
import ru.util.TaskManager;

import java.util.List;

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
		return "С неба начинает падать динамит! В аду безопасно.";
	}

	@Override
	public void onChoose() {
		reset();
	}

	public void reset() {
		timeToDrop = MathUtils.randomRange(20, 35);
	}

	private Location getRandomLocation() {
		List<UHCPlayer> availablePlayers = PlayerManager.getAlivePlayers();
		availablePlayers.removeIf(player -> player.getLocation() == null || player.getLocation().getWorld() == WorldManager.getGameMapNether());
		if(availablePlayers.isEmpty()) return null;
		UHCPlayer target = MathUtils.choose(availablePlayers);
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
					boolean closedArena = UHC.state == GameState.DEATHMATCH && !ArenaManager.getCurrentArena().isOpen();
					if(dropLocation != null && !closedArena) {
						TNTPrimed tnt = (TNTPrimed) dropLocation.getWorld().spawnEntity(dropLocation, EntityType.PRIMED_TNT);
						tnt.setFuseTicks(12 * 20);
					}
					reset();
				}
			}
		}
	}

}
