package ru.mutator;

import org.bukkit.GameRule;
import org.bukkit.Material;
import ru.UHC.WorldManager;

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
