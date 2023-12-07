package ru.greenbudgie.artifact;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.greenbudgie.UHC.PlayerManager;

import javax.annotation.Nullable;

public class ArtifactHunger extends Artifact {

	private static final int FOOD_LEVEL = 3;

	@Override
	public String getName() {
		return "Всемирный Голод";
	}

	@Override
	public String getDescription() {
		return "У всех игроков шкала голода резко падает до 3х единиц";
	}

	@Override
	public int getStartingPrice() {
		return 10;
	}

	@Override
	public float getPriceIncreaseAmount() {
		return 1;
	}

	@Override
	public boolean onUse(@Nullable Player player) {
		for(Player currentPlayer : PlayerManager.getAliveOnlinePlayers()) {
			if(currentPlayer.getFoodLevel() <= FOOD_LEVEL) {
				continue;
			}
			if(currentPlayer.getSaturation() > FOOD_LEVEL) {
				currentPlayer.setSaturation(FOOD_LEVEL);
			}
			currentPlayer.setFoodLevel(FOOD_LEVEL);
		}
		for(Player currentPlayer : PlayerManager.getInGamePlayersAndSpectators()) {
			currentPlayer.playSound(currentPlayer.getLocation(), Sound.ENTITY_CAT_DEATH, 1F, 0.5F);
		}
		return true;
	}

	@Override
	public Material getType() {
		return Material.CHICKEN;
	}

}
