package ru.greenbudgie.artifact;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.WorldManager;
import ru.greenbudgie.mutator.MutatorManager;

import javax.annotation.Nullable;

public class ArtifactTimeLeap extends Artifact {

	@Override
	public String getName() {
		return "Временная Петля";
	}

	@Override
	public String getDescription() {
		return "Меняет день на ночь или ночь на день";
	}

	@Override
	public int getStartingPrice() {
		return 5;
	}

	@Override
	public float getPriceIncreaseAmount() {
		return 0;
	}

	@Override
	public boolean onUse(@Nullable Player player) {
		if(MutatorManager.isActive(MutatorManager.eternalNight) || MutatorManager.isActive(MutatorManager.eternalDay)) {
			return false;
		}
		WorldManager.getGameMap().setTime(WorldManager.getGameMap().getTime() + 12000);
		for(Player currentPlayer : PlayerManager.getInGamePlayersAndSpectators()) {
			currentPlayer.playSound(currentPlayer.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1F, 0.8F);
		}
		return true;
	}

	@Override
	public Material getType() {
		return Material.SUNFLOWER;
	}

}
