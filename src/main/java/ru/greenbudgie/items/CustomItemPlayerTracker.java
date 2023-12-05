package ru.greenbudgie.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import ru.greenbudgie.util.ItemInfo;

public class CustomItemPlayerTracker extends RequesterCustomItem {

	public String getName() {
		return ChatColor.GREEN + "Player Tracker";
	}

	public Material getMaterial() {
		return Material.COMPASS;
	}

	@Override
	public ItemInfo getDescription() {
		return new ItemInfo("Указывает на ближайшего врага, когда держишь его в руке")
				.note("Не действует в аду, т.к. в аду компасы не работают");
	}

	@Override
	public int getRedstonePrice() {
		return 128;
	}

	@Override
	public int getLapisPrice() {
		return 20;
	}

}
