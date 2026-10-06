package ru.greenbudgie.artifact;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.drop.Drop;
import ru.greenbudgie.drop.Drops;

import javax.annotation.Nullable;

public class ArtifactDropThief extends Artifact {

	@Override
	public String getName() {
		return "Вор дропов";
	}

	@Override
	public String getDescription() {
		return "Меняет локации эирдропа, кейвдропа и незердропа";
	}

	@Override
	public int getStartingPrice() {
		return 5;
	}

	@Override
	public float getPriceIncreaseAmount() {
		return 0.5f;
	}

	@Override
	public boolean onUse(@Nullable Player player) {
		for(Drop drop : Drops.DROPS) {
			drop.setLocation(drop.getRandomLocation());
		}
		for (Player currentPlayer : PlayerManager.getInGamePlayersAndSpectators()) {
			currentPlayer.playSound(currentPlayer.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1F, 1.5F);
		}

		return true;
	}

	@Override
	public Material getType() {
		return Material.PHANTOM_MEMBRANE;
	}

	@Override
	public boolean canBeUsedOnArena() {
		return false;
	}

}
