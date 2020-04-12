package ru.mutator;

import org.bukkit.Material;

public class MutatorSimpleRequests extends Mutator {

	@Override
	public Material getItemToShow() {
		return Material.REDSTONE_BLOCK;
	}

	@Override
	public String getName() {
		return "Щедрые запросы";
	}

	@Override
	public String getDescription() {
		return "Всем запросам требуется лишь редстоун";
	}

}
