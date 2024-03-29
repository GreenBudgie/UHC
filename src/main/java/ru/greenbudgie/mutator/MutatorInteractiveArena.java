package ru.greenbudgie.mutator;

import org.bukkit.Material;
import ru.greenbudgie.mutator.base.Mutator;
import ru.greenbudgie.mutator.base.ThreatStatus;

public class MutatorInteractiveArena extends Mutator {

	@Override
	public Material getItemToShow() {
		return Material.COBBLESTONE;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.INNOCENT;
	}

	@Override
	public String getName() {
		return "Интерактивная Арена";
	}

	@Override
	public boolean canWorkIfArenaIsClosed() {
		return false;
	}

	@Override
	public String getDescription() {
		return "Во время ДМ на арене можно будет ломать и ставить блоки. Однако, со сломанных блоков не будет падать дроп. Не выпадает, если арена закрытая.";
	}

}
