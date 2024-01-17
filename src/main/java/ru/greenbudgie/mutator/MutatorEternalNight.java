package ru.greenbudgie.mutator;

import org.bukkit.GameRule;
import org.bukkit.Material;
import ru.greenbudgie.UHC.WorldManager;
import ru.greenbudgie.mutator.base.Mutator;
import ru.greenbudgie.mutator.base.ThreatStatus;
import ru.greenbudgie.mutator.manager.MutatorManager;

public class MutatorEternalNight extends Mutator {

	@Override
	public Material getItemToShow() {
		return Material.END_STONE;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.CRITICAL;
	}

	@Override
	public String getName() {
		return "Вечная Ночь";
	}

	@Override
	public String getDescription() {
		return "На протяжении всей игры будет ночь. Артефакт не поможет!";
	}

	@Override
	public boolean conflictsWith(Mutator another) {
		return another == MutatorManager.eternalDay;
	}

	@Override
	public void onChoose() {
		WorldManager.getGameMap().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
		WorldManager.getGameMap().setTime(18000);
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
