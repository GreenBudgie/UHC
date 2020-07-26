package ru.artifact;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.UHC.GameState;
import ru.UHC.UHC;
import ru.UHC.WorldManager;

import javax.annotation.Nullable;

public class ArtifactTime extends Artifact {

	@Override
	public String getName() {
		return ChatColor.DARK_GREEN + "Повелитель Времени";
	}

	@Override
	public String getDescription() {
		return "Уменьшает время до ДМ или до включения ПВП в полтора раза";
	}

	@Override
	public int getStartingPrice() {
		return 13;
	}

	@Override
	public float getPriceIncreaseAmount() {
		return 2;
	}

	@Override
	public void onUse(@Nullable Player player) {
		if(UHC.state == GameState.GAME) {
			UHC.deathmatchTimer /= 1.5;
		} else if(UHC.state == GameState.OUTBREAK) {
			UHC.outbreakTimer /= 1.5;
		}
		if(player != null) player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1F, 0.6F);
	}

	@Override
	public Material getType() {
		return Material.GOLDEN_SWORD;
	}

}
