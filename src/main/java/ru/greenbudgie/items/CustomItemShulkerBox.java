package ru.greenbudgie.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import ru.greenbudgie.util.ItemInfo;

public class CustomItemShulkerBox extends RequesterCustomItem {

	public String getName() {
		return ChatColor.GREEN + "" + ChatColor.ITALIC + "Shulker Box";
	}

	public Material getMaterial() {
		return Material.LIME_SHULKER_BOX;
	}

	@Override
	public ItemInfo getDescription() {
		return new ItemInfo("Обычный шалкер бокс");
	}

	@Override
	public int getRedstonePrice() {
		return 20;
	}

	@Override
	public int getLapisPrice() {
		return 0;
	}

	@Override
	public boolean isGlowing() {
		return false;
	}
}
