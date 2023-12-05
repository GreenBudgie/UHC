package ru.greenbudgie.artifact;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.greenbudgie.UHC.GameType;
import ru.greenbudgie.mutator.MutatorManager;

import javax.annotation.Nullable;

public class ArtifactMutator extends Artifact {

	@Override
	public String getName() {
		return ChatColor.DARK_RED + "Преображение Игры";
	}

	@Override
	public String getDescription() {
		return "Активирует новый случайный мутатор. Не сработает, если уже активировано 6 мутаторов!";
	}

	@Override
	public int getStartingPrice() {
		return 12;
	}

	@Override
	public float getPriceIncreaseAmount() {
		return 1;
	}

	@Override
	public boolean onUse(@Nullable Player player) {
		if(!GameType.getType().allowsMutators()) return false;
		if(MutatorManager.activeMutators.size() < 6) {
			if(player != null) player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.8F, 1F);
			MutatorManager.activateRandomArtifactMutator();
			return true;
		}
		return false;
	}

	@Override
	public Material getType() {
		return Material.REDSTONE;
	}

}