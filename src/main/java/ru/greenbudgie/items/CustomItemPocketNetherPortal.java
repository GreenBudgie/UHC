package ru.greenbudgie.items;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.block.CustomBlockPocketNetherPortal;
import ru.greenbudgie.util.item.ItemInfo;

public class CustomItemPocketNetherPortal extends RequesterCustomItem implements BlockHolder {

	public String getName() {
		return ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Pocket Nether Portal";
	}

	public Material getMaterial() {
		return Material.OBSIDIAN;
	}

	@Override
	public ItemInfo getDescription() {
		return new ItemInfo("Не можешь найти лаву? Нет гравия? Используй карманный портал в ад!")
				.extra("При установке строится и активируется портал в ад");
	}

	@Override
	public int getRedstonePrice() {
		return 52;
	}

	@Override
	public int getLapisPrice() {
		return 12;
	}

	@Override
	public boolean placeBlock(Location location, Player owner) {
		new CustomBlockPocketNetherPortal(location, PlayerManager.asUHCPlayer(owner));
		return true;
	}

	@Override
	public boolean canPlaceOnDeathmatch() {
		return false;
	}
}
