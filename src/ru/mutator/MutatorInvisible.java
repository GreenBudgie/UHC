package ru.mutator;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.UHC.UHC;

public class MutatorInvisible extends EffectBasedMutator {

	@Override
	public Material getItemToShow() {
		return Material.BONE_MEAL;
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
	public PotionEffectType getEffect() {
		return PotionEffectType.INVISIBILITY;
	}

	@Override
	public int getAmplifier() {
		return 0;
	}
}
