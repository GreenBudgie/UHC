package ru.mutator;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.UHC.UHC;
import ru.util.WorldHelper;

public class MutatorHyperSpeed extends EffectBasedMutator {

	@Override
	public Material getItemToShow() {
		return Material.FEATHER;
	}

	@Override
	public String getName() {
		return "�������������";
	}

	@Override
	public String getDescription() {
		return "�� ��� ���� ���� ������� �������� �������� II";
	}

	@Override
	public PotionEffectType getEffect() {
		return PotionEffectType.SPEED;
	}

	@Override
	public int getAmplifier() {
		return 1;
	}

}
