package ru.greenbudgie.mutator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldBorder;
import ru.greenbudgie.UHC.WorldManager;
import ru.greenbudgie.drop.Drop;
import ru.greenbudgie.drop.Drops;

public class MutatorSmallMap extends Mutator {

	@Override
	public Material getItemToShow() {
		return Material.OAK_FENCE;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.DANGEROUS;
	}

	@Override
	public String getName() {
		return "Ограничение";
	}

	@Override
	public String getDescription() {
		return "Размер карты уменьшается в два раза";
	}

	@Override
	public void onChoose() {
		WorldBorder overworldBorder = WorldManager.getGameMap().getWorldBorder();
		WorldBorder netherBorder = WorldManager.getGameMapNether().getWorldBorder();
		overworldBorder.setSize(overworldBorder.getSize() / 2);
		netherBorder.setSize(netherBorder.getSize() / 2);
		for(Drop drop : Drops.DROPS) {
			Location dropLocation = drop.getLocation();
			if(dropLocation == null || dropLocation.getWorld() == null) continue;
			if(!dropLocation.getWorld().getWorldBorder().isInside(dropLocation)) {
				drop.setLocation(drop.getRandomLocation());
			}
		}
	}

	@Override
	public void onDeactivate() {
		WorldBorder overworldBorder = WorldManager.getGameMap().getWorldBorder();
		WorldBorder netherBorder = WorldManager.getGameMapNether().getWorldBorder();
		overworldBorder.setSize(overworldBorder.getSize() * 2);
		netherBorder.setSize(netherBorder.getSize() * 2);
	}

	@Override
	public boolean canBeAddedFromArtifact() {
		return false;
	}

}
