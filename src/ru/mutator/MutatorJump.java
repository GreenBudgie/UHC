package ru.mutator;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.UHC.UHC;

public class MutatorJump extends EffectBasedMutator {

	@Override
	public Material getItemToShow() {
		return Material.RABBIT_FOOT;
	}

	@Override
	public String getName() {
		return "� �������";
	}

	@Override
	public String getDescription() {
		return "���� ������� �� ��� ���� �������� ���������� X";
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
