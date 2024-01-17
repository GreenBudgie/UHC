package ru.greenbudgie.mutator;

import org.bukkit.Material;
import ru.greenbudgie.mutator.base.Mutator;
import ru.greenbudgie.mutator.base.ThreatStatus;

public class MutatorSimpleRequests extends Mutator {

	@Override
	public Material getItemToShow() {
		return Material.REDSTONE_BLOCK;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.SUPPORTING;
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
