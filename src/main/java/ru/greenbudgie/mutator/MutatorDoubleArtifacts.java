package ru.greenbudgie.mutator;

import org.bukkit.Material;
import ru.greenbudgie.mutator.base.Mutator;
import ru.greenbudgie.mutator.base.ThreatStatus;

public class MutatorDoubleArtifacts extends Mutator {

	@Override
	public Material getItemToShow() {
		return Material.DRIED_KELP;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.SUPPORTING;
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
