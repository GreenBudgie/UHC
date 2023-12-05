package ru.greenbudgie.mutator;

import org.bukkit.Material;

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
