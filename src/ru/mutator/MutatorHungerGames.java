package ru.mutator;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.UHC.UHC;

public class MutatorHungerGames extends Mutator {

	@Override
	public Material getItemToShow() {
		return Material.DIAMOND_SWORD;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.DANGEROUS;
	}

	@Override
	public String getName() {
		return "Голодные Игры";
	}

	@Override
	public String getDescription() {
		return "Время до начала ПВП сокращается до одной минуты";
	}

	@Override
	public boolean canBeAddedFromArtifact() {
		return false;
	}

	@Override
	public boolean canBeDeactivatedByArtifact() {
		return false;
	}

	@Override
	public boolean canBeHidden() {
		return false;
	}

}
