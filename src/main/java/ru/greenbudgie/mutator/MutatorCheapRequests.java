package ru.greenbudgie.mutator;

import org.bukkit.Material;
import ru.greenbudgie.mutator.base.Mutator;
import ru.greenbudgie.mutator.base.ThreatStatus;

public class MutatorCheapRequests extends Mutator {

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
		return "Распродажа";
	}

	@Override
	public String getDescription() {
		return "Всем предметам в магазине требуется лишь редстоун, а еще они со скидкой 50%";
	}

}
