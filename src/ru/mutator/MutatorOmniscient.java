package ru.mutator;

import org.bukkit.GameRule;
import org.bukkit.Material;
import ru.UHC.UHC;
import ru.UHC.WorldManager;

public class MutatorOmniscient extends Mutator {

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.INNOCENT;
	}

	@Override
	public Material getItemToShow() {
		return Material.ENDER_EYE;
	}

	@Override
	public String getName() {
		return "Всевидящий";
	}

	@Override
	public String getDescription() {
		return "Все знают прогресс других игроков. В чат выводятся полученные достижения; /inv <ник> для просмотра инвентаря";
	}

	@Override
	public boolean canBeHidden() {
		return false;
	}

	@Override
	public void onChoose() {
		UHC.resetAllAdvancements();
		WorldManager.getGameMap().setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, true);
	}

	@Override
	public void onDeactivate() {
		WorldManager.getGameMap().setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
	}
}
