package ru.mutator;

import org.bukkit.Material;

public class MutatorDoubleArtifacts extends Mutator {

	@Override
	public Material getItemToShow() {
		return Material.DRIED_KELP;
	}

	@Override
	public String getName() {
		return "Темные Дела";
	}

	@Override
	public String getDescription() {
		return "С мобов выпадает в два раза больше артефактов";
	}

}
