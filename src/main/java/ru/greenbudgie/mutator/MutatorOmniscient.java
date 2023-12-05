package ru.greenbudgie.mutator;

import org.bukkit.Material;

public class MutatorOmniscient extends Mutator {

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.INNOCENT;
	}

	@Override
	public Material getItemToShow() {
		return Material.ENDER_EYE;
	}

	@Override
	public String getName() {
		return "Всевидящий";
	}

	@Override
	public String getDescription() {
		return "Все видят инвентари других игроков. /inv <ник> для просмотра инвентаря";
	}

	@Override
	public boolean canBeHidden() {
		return false;
	}

}
