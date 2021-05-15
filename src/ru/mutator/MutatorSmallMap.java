package ru.mutator;

import org.bukkit.Material;
import org.bukkit.WorldBorder;
import ru.UHC.WorldManager;
import ru.drop.Drop;
import ru.drop.Drops;

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
		WorldBorder border = WorldManager.getGameMap().getWorldBorder();
		border.setSize(border.getSize() / 2);
		for(Drop drop : Drops.DROPS) {
			drop.setLocation(drop.getRandomLocation());
		}
	}

	@Override
	public void onDeactivate() {
		WorldBorder border = WorldManager.getGameMap().getWorldBorder();
		border.setSize(border.getSize() * 2);
	}

	@Override
	public boolean canBeAddedFromArtifact() {
		return false;
	}

}
