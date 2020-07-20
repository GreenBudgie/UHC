package ru.artifact;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.UHC.UHC;
import ru.util.WorldHelper;

public class ArtifactTeleport extends Artifact {

	@Override
	public String getName() {
		return ChatColor.LIGHT_PURPLE + "����������";
	}

	@Override
	public String getDescription() {
		return "������������� ���� ������� � ��������� ����� � ������� 80 ������";
	}

	@Override
	public int getStartingPrice() {
		return 12;
	}

	@Override
	public float getPriceIncreaseAmount() {
		return 0;
	}

	@Override
	public void onUse(Player p) {
		for(Player player : UHC.players) {
			WorldHelper.chorusTeleport(player, 80);
		}
	}

	@Override
	public Material getType() {
		return Material.CHORUS_FRUIT;
	}

}
