package ru.mutator;

import org.bukkit.Material;

public class MutatorInteractiveArena extends Mutator {

	@Override
	public Material getItemToShow() {
		return Material.COBBLESTONE;
	}

	@Override
	public String getName() {
		return "������������� �����";
	}

	@Override
	public String getDescription() {
		return "�� ����� �� �� ����� ����� ����� ������ � ������� �����, � ����� ������������ ��������� � �����. ������, �� ��������� ������ �� ����� ������ ����.";
	}

}
