package ru.mutator;

import org.bukkit.Material;

public class MutatorDoubleArtifacts extends Mutator {

	@Override
	public Material getItemToShow() {
		return Material.DRIED_KELP;
	}

	@Override
	public String getName() {
		return "������ ����";
	}

	@Override
	public String getDescription() {
		return "� ����� �������� � ��� ���� ������ ����������";
	}

}
