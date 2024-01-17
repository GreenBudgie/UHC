package ru.greenbudgie.mutator;

import org.bukkit.GameRule;
import org.bukkit.Material;
import ru.greenbudgie.UHC.WorldManager;
import ru.greenbudgie.mutator.base.Mutator;
import ru.greenbudgie.mutator.base.ThreatStatus;
import ru.greenbudgie.mutator.manager.MutatorManager;

public class MutatorEternalDay extends Mutator {

	@Override
	public Material getItemToShow() {
		return Material.SUNFLOWER;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.SUPPORTING;
	}

	@Override
	public String getName() {
		return "Вечный День";
	}

	@Override
	public String getDescription() {
		return "На протяжении всей игры будет день. Артефакт не поможет!";
	}

	@Override
	public boolean conflictsWith(Mutator another) {
		return another == MutatorManager.eternalNight;
	}

	@Override
	public void onChoose() {
		WorldManager.getGameMap().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
		WorldManager.getGameMap().setTime(6000);
	}

	@Override
	public void onDeactivate() {
		WorldManager.getGameMap().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
	}

	@Override
	public boolean canBeHidden() {
		return false;
	}

}
