package ru.mutator;

import org.bukkit.Material;

public class MutatorHealthDisplay extends Mutator {

	@Override
	public Material getItemToShow() {
		return Material.RED_DYE;
	}

	@Override
	public String getName() {
		return "Отображение Здоровья";
	}

	@Override
	public String getDescription() {
		return "Под никами игроков и в табе выводится количество их здоровья";
	}

	@Override
	public boolean canBeHidden() {
		return false;
	}

}
