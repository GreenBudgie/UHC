package ru.artifact;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;
import ru.UHC.Drops;

public class ArtifactAirdrop extends Artifact {

	@Override
	public String getName() {
		return ChatColor.DARK_AQUA + "�������� ����";
	}

	@Override
	public String getDescription() {
		return "�������� ������� ��������� ���������� �������� � ��������� ����� ��� �������� � 3 ����";
	}

	@Override
	public int getStartingPrice() {
		return 8;
	}

	@Override
	public float getPriceIncreaseAmount() {
		return 0;
	}

	@Override
	public void onUse(@Nullable Player player) {
		Drops.chooseAirdropLocation();
		Drops.airdropTimer /= 3;
		if(player != null) {
			player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1F, 1.5F);
		}
	}

	@Override
	public Material getType() {
		return Material.BEACON;
	}

}
