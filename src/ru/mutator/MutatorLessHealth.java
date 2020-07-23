package ru.mutator;

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
		return "�������� �����!";
	}

	@Override
	public String getDescription() {
		return "� ������ ���� ������ ���� 3 ������";
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
