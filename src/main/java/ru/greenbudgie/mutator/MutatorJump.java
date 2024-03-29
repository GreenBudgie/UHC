package ru.greenbudgie.mutator;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;
import ru.greenbudgie.mutator.base.EffectBasedMutator;
import ru.greenbudgie.mutator.base.ThreatStatus;

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
		return "Всем игрокам на всю игру выдается прыгучесть V";
	}

	@Override
	public EffectEntry[] getEffects() {
		return new EffectEntry[] { new EffectEntry(PotionEffectType.JUMP, 4) };
	}

}
