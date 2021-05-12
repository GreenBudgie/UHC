package ru.mutator;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.UHC.UHC;

public class MutatorGlowing extends EffectBasedMutator {

	@Override
	public Material getItemToShow() {
		return Material.GLOWSTONE_DUST;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.INNOCENT;
	}

	@Override
	public String getName() {
		return "Сияние";
	}

	@Override
	public String getDescription() {
		return "Все игроки подсвечиваются";
	}

	@Override
	public boolean canBeHidden() {
		return false;
	}

	@Override
	public boolean conflictsWith(Mutator another) {
		return another == MutatorManager.invisible;
	}

	@Override
	public PotionEffectType getEffect() {
		return PotionEffectType.GLOWING;
	}

	@Override
	public int getAmplifier() {
		return 0;
	}
}
