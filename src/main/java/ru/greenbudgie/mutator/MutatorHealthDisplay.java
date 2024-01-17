package ru.greenbudgie.mutator;

import org.bukkit.Material;
import ru.greenbudgie.mutator.base.Mutator;
import ru.greenbudgie.mutator.base.ThreatStatus;

public class MutatorHealthDisplay extends Mutator {

	@Override
	public Material getItemToShow() {
		return Material.RED_DYE;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.INNOCENT;
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
