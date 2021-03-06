package ru.mutator;

import org.bukkit.Material;
import ru.drop.Drop;
import ru.drop.Drops;

public class MutatorDrop extends Mutator {

	@Override
	public Material getItemToShow() {
		return Material.BEACON;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.SUPPORTING;
	}

	@Override
	public String getName() {
		return "Больше Дропа!";
	}

	@Override
	public String getDescription() {
		return "Все дропы (аирдроп, кейвдроп и незердроп) спавнятся в два раза чаще";
	}

	@Override
	public void onChoose() {
		for(Drop drop : Drops.DROPS) {
			drop.setTimer(drop.getTimer() / 2);
		}
	}

	@Override
	public boolean canBeHidden() {
		return false;
	}

}
