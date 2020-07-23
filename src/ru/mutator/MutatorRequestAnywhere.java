package ru.mutator;

import org.bukkit.Material;

public class MutatorRequestAnywhere extends Mutator {

	@Override
	public Material getItemToShow() {
		return Material.GLASS_PANE;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.SUPPORTING;
	}

	@Override
	public String getName() {
		return "���������� �����";
	}

	@Override
	public String getDescription() {
		return "������� ����� ������ ���� ��� ������";
	}

}
