package ru.greenbudgie.mutator;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;
import ru.greenbudgie.mutator.base.EffectBasedMutator;
import ru.greenbudgie.mutator.base.Mutator;
import ru.greenbudgie.mutator.base.ThreatStatus;
import ru.greenbudgie.mutator.manager.MutatorManager;

public class MutatorInvisible extends EffectBasedMutator {

	@Override
	public Material getItemToShow() {
		return Material.BONE_MEAL;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.INNOCENT;
	}

	@Override
	public String getName() {
		return "Игра в Прятки";
	}

	@Override
	public String getDescription() {
		return "Всем игрокам на всю игру выдается невидимость";
	}

	@Override
	public boolean conflictsWith(Mutator another) {
		return another == MutatorManager.glowing;
	}

	@Override
	public EffectEntry[] getEffects() {
		return new EffectEntry[] { new EffectEntry(PotionEffectType.INVISIBILITY, 0) };
	}

}
