package ru.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public class CustomItemDarkArtifact extends CustomItem {

	@Override
	public String getName() {
		return ChatColor.DARK_RED + "" + ChatColor.BOLD + "������ ��������";
	}

	@Override
	public Material getMaterial() {
		return Material.DRIED_KELP;
	}

}
