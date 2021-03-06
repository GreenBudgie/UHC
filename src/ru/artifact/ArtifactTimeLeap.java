package ru.artifact;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.UHC.WorldManager;
import ru.mutator.MutatorManager;

import javax.annotation.Nullable;

public class ArtifactTimeLeap extends Artifact {

	@Override
	public String getName() {
		return ChatColor.YELLOW + "Временная Петля";
	}

	@Override
	public String getDescription() {
		return "Меняет день на ночь или ночь на день";
	}

	@Override
	public int getStartingPrice() {
		return 4;
	}

	@Override
	public float getPriceIncreaseAmount() {
		return 0;
	}

	@Override
	public void onUse(@Nullable Player player) {
		if(MutatorManager.isActive(MutatorManager.eternalNight) || MutatorManager.isActive(MutatorManager.eternalDay)) {
			if(player != null) player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_BURP, 0.5F, 0.5F);
		} else {
			WorldManager.getGameMap().setTime(WorldManager.getGameMap().getTime() + 12000);
			if(player != null) player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1F, 0.8F);
		}
	}

	@Override
	public Material getType() {
		return Material.SUNFLOWER;
	}

}
