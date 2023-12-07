package ru.greenbudgie.artifact;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.drop.Drop;
import ru.greenbudgie.drop.Drops;

import javax.annotation.Nullable;

public class ArtifactDrop extends Artifact {

	@Override
	public String getName() {
		return "Раздача";
	}

	@Override
	public String getDescription() {
		return "Сокращает время ожидания всех дропов (аирдропа, кейвдропа и незердропа) в 3 раза";
	}

	@Override
	public int getStartingPrice() {
		return 8;
	}

	@Override
	public float getPriceIncreaseAmount() {
		return 1;
	}

	@Override
	public boolean onUse(@Nullable Player player) {
		for(Drop drop : Drops.DROPS) {
			drop.setTimer(drop.getTimer() / 3);
		}
		for(Player currentPlayer : PlayerManager.getInGamePlayersAndSpectators()) {
			currentPlayer.playSound(currentPlayer.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1F, 1.5F);
		}
		return true;
	}

	@Override
	public Material getType() {
		return Material.BEACON;
	}

	@Override
	public boolean canBeUsedOnArena() {
		return false;
	}
}
