package ru.greenbudgie.mutator;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;

public class MutatorJump extends EffectBasedMutator {

	@Override
	public Material getItemToShow() {
		return Material.RABBIT_FOOT;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.SUPPORTING;
	}

	@Override
	public String getName() {
		return "К Небесам";
	}

	@Override
	public String getDescription() {
		return "Всем игрокам на всю игру выдается прыгучесть X";
	}

	@Override
	public PotionEffectType getEffect() {
		return PotionEffectType.JUMP;
	}

	@Override
	public int getAmplifier() {
		return 9;
	}

}