package ru.greenbudgie.mutator;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;
import ru.greenbudgie.mutator.base.EffectBasedMutator;
import ru.greenbudgie.mutator.base.Mutator;
import ru.greenbudgie.mutator.base.ThreatStatus;
import ru.greenbudgie.mutator.manager.MutatorManager;

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
	public boolean conflictsWith(Mutator another) {
		return another == MutatorManager.invisible;
	}

	@Override
	public boolean applyToOfflinePlayerGhosts() {
		return true;
	}

	@Override
	public EffectEntry[] getEffects() {
		return new EffectEntry[] { new EffectEntry(PotionEffectType.GLOWING, 0) };
	}

}
