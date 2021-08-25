package ru.artifact;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.UHC.PlayerManager;
import ru.UHC.UHC;

import javax.annotation.Nullable;

public class ArtifactHunger extends Artifact {

	@Override
	public String getName() {
		return ChatColor.LIGHT_PURPLE + "Всемирный Голод";
	}

	@Override
	public String getDescription() {
		return "У всех игроков шкала голода резко падает до 2х единиц";
	}

	@Override
	public int getStartingPrice() {
		return 13;
	}

	@Override
	public float getPriceIncreaseAmount() {
		return 1;
	}

	@Override
	public boolean onUse(@Nullable Player player) {
		for(Player currentPlayer : PlayerManager.getAliveOnlinePlayers()) {
			if(currentPlayer.getFoodLevel() > 2) {
				if(currentPlayer.getSaturation() > 2) currentPlayer.setSaturation(2);
				currentPlayer.setFoodLevel(2);
			}
		}
		if(player != null) {
			player.playSound(player.getLocation(), Sound.ENTITY_CAT_DEATH, 1F, 0.5F);
		}
		return true;
	}

	@Override
	public Material getType() {
		return Material.CHICKEN;
	}

}
