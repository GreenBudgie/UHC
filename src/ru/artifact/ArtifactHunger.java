package ru.artifact;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.UHC.Drops;
import ru.UHC.UHC;

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
	public void onUse(Player p) {
		for(Player player : UHC.players) {
			if(player.getFoodLevel() > 2) {
				if(player.getSaturation() > 2) player.setSaturation(2);
				player.setFoodLevel(2);
			}
		}
		p.playSound(p.getLocation(), Sound.ENTITY_CAT_DEATH, 1F, 0.5F);
	}

	@Override
	public Material getType() {
		return Material.CHICKEN;
	}

}
