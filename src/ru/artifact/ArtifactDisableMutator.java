package ru.artifact;

import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.UHC.UHC;
import ru.mutator.Mutator;
import ru.mutator.MutatorManager;
import ru.util.MathUtils;

import java.util.List;

public class ArtifactDisableMutator extends Artifact {

	@Override
	public String getName() {
		return ChatColor.RED + "����� ����� ����";
	}

	@Override
	public String getDescription() {
		return "������� ��������� �������. �� ���������, ���� ������� ��������� �� ������������, ���� ������ �������������� �� ���� �� ���.";
	}

	@Override
	public int getStartingPrice() {
		return 13;
	}

	@Override
	public float getPriceIncreaseAmount() {
		return 1;
	}

	@Override
	public void onUse(Player p) {
		List<Mutator> mutators = MutatorManager.getMutatorsForDeactivation();
		if(mutators.size() > 0) {
			p.playSound(p.getLocation(), Sound.ITEM_TOTEM_USE, 0.8F, 1.2F);
			Mutator mutator = MathUtils.choose(mutators);
			for(Player player : UHC.getInGamePlayers()) {
				player.sendMessage(ChatColor.YELLOW + "��� ������������� �������: " + ChatColor.LIGHT_PURPLE + mutator.getName());
			}
			mutator.deactivate();
		} else {
			p.sendMessage(ChatColor.RED + "���������� �������������� �� ���� �������!");
		}
	}

	@Override
	public Material getType() {
		return Material.GUNPOWDER;
	}

}
