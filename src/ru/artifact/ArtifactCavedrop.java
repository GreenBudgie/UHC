package ru.artifact;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.UHC.Drops;
import ru.UHC.UHC;

public class ArtifactCavedrop extends Artifact {

	@Override
	public String getName() {
		return ChatColor.RED + "Подземные Дары";
	}

	@Override
	public String getDescription() {
		return "Изменяет позицию выпадения следующего кейвдропа и сокращает время его ожидания в 3 раза";
	}

	@Override
	public int getStartingPrice() {
		return 8;
	}

	@Override
	public float getPriceIncreaseAmount() {
		return 0;
	}

	@Override
	public void onUse(Player p) {
		Drops.chooseCavedropLocation();
		Drops.cavedropTimer /= 3;
		p.playSound(p.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1F, 1.2F);
	}

	@Override
	public Material getType() {
		return Material.CHEST;
	}

}
