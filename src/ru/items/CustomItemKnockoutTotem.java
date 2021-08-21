package ru.items;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.block.CustomBlockKnockoutTotem;
import ru.block.CustomBlockLandmine;

public class CustomItemKnockoutTotem extends RequesterCustomItem implements BlockHolder {

	public String getName() {
		return ChatColor.GRAY + "" + ChatColor.BOLD +"Knockout Totem";
	}

	public Material getMaterial() {
		return Material.DEEPSLATE_TILE_WALL;
	}

	@Override
	public String getDescription() {
		return "Тотем отбрасывания. При установке разбрасывает в стороны всех живых существ и игроков, кроме тебя, в радиусе 6 блоков. Действует 30 секунд.";
	}

	@Override
	public int getRedstonePrice() {
		return 64;
	}

	@Override
	public int getLapisPrice() {
		return 12;
	}

	@Override
	public void placeBlock(Location location, Player owner) {
		new CustomBlockKnockoutTotem(location, owner);
	}

	@Override
	public boolean canPlaceOnDeathmatch() {
		return true;
	}
}
