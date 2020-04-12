package ru.mutator;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.UHC.UHC;

public class MutatorHaste extends EffectBasedMutator {

	@Override
	public Material getItemToShow() {
		return Material.GOLDEN_PICKAXE;
	}

	@Override
	public String getName() {
		return "Дикий Шахтер";
	}

	@Override
	public String getDescription() {
		return "Всем игрокам выдается Спешка II";
	}

	@Override
	public PotionEffectType getEffect() {
		return PotionEffectType.FAST_DIGGING;
	}

	@Override
	public int getAmplifier() {
		return 1;
	}

}
