package ru.artifact;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.mutator.MutatorManager;

public class ArtifactMutator extends Artifact {

	@Override
	public String getName() {
		return ChatColor.DARK_RED + "������������ ����";
	}

	@Override
	public String getDescription() {
		return "���������� ����� ��������� �������. �� ���������, ���� ��� ������������ 6 ���������!";
	}

	@Override
	public int getStartingPrice() {
		return 12;
	}

	@Override
	public float getPriceIncreaseAmount() {
		return 1;
	}

	@Override
	public void onUse(Player p) {
		if(MutatorManager.activeMutators.size() < 6) {
			p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.8F, 1F);
			MutatorManager.activateRandomArtifactMutator();
		} else {
			p.sendMessage(ChatColor.RED + "������� �� ��� �����������!");
		}
	}

	@Override
	public Material getType() {
		return Material.REDSTONE;
	}

}
