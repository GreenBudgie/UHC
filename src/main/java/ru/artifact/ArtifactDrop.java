package ru.artifact;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.drop.Drop;
import ru.drop.Drops;

import javax.annotation.Nullable;

public class ArtifactDrop extends Artifact {

	@Override
	public String getName() {
		return ChatColor.DARK_AQUA + "Раздача";
	}

	@Override
	public String getDescription() {
		return "Изменяет позицию выпадения всех дропов (аирдропа, кейвдропа и незердропа) и сокращает время их ожидания в 3 раза.";
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
			drop.setLocation(drop.getRandomLocation());
			drop.setTimer(drop.getTimer() / 3);
		}
		if(player != null) {
			player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1F, 1.5F);
		}
		return true;
	}

	@Override
	public Material getType() {
		return Material.BEACON;
	}

}
