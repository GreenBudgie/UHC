package ru.greenbudgie.mutator;

import org.bukkit.Material;

public class MutatorLessHealth extends Mutator {

	@Override
	public Material getItemToShow() {
		return Material.RED_MUSHROOM;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.CRITICAL;
	}

	@Override
	public String getName() {
		return "Отличный Старт!";
	}

	@Override
	public String getDescription() {
		return "В начале игры здоровье уменьшено вдвое";
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
