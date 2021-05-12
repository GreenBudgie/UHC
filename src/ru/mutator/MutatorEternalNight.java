package ru.mutator;

import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.UHC.UHC;
import ru.UHC.WorldManager;

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
