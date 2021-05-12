package ru.artifact;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.UHC.UHC;
import ru.util.WorldHelper;

import javax.annotation.Nullable;

public class ArtifactTeleport extends Artifact {

	@Override
	public String getName() {
		return ChatColor.LIGHT_PURPLE + "Переброска";
	}

	@Override
	public String getDescription() {
		return "Телепортирует всех игроков в случайную точку в радиусе 80 блоков";
	}

	@Override
	public int getStartingPrice() {
		return 12;
	}

	@Override
	public float getPriceIncreaseAmount() {
		return 0;
	}

	@Override
	public void onUse(@Nullable Player player) {
		for(Player currentPlayer : UHC.players) {
			WorldHelper.chorusTeleport(currentPlayer, 80);
		}
	}

	@Override
	public Material getType() {
		return Material.CHORUS_FRUIT;
	}

}
