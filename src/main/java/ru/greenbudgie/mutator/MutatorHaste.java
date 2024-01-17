package ru.greenbudgie.mutator;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;
import ru.greenbudgie.mutator.base.EffectBasedMutator;
import ru.greenbudgie.mutator.base.ThreatStatus;

public class MutatorHaste extends EffectBasedMutator {

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.SUPPORTING;
	}

	@Override
	public Material getItemToShow() {
		return Material.SUGAR;
	}

	@Override
	public String getName() {
		return "Гиперскорость";
	}

	@Override
	public String getDescription() {
		return "Все игроки становятся буквально быстрее. Выдается Скорость и Спешка II";
	}

	@Override
	public EffectEntry[] getEffects() {
		return new EffectEntry[] {
				new EffectEntry(PotionEffectType.SPEED, 1),
				new EffectEntry(PotionEffectType.FAST_DIGGING, 1)
		};
	}

}
